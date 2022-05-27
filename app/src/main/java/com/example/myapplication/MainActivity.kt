package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import android.provider.MediaStore

import android.content.ContentValues
import android.os.Build
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button


class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Проверка разрешений и запрос, если отсутсвуют
        if (allPermissionsGranted()) {
//            Запуск камеры, если все имеются необходимые разрешения
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Настройка обработчика нажатия на кнопку
        findViewById<Button>(R.id.photo_btn).setOnClickListener { takePhoto() }

//        Испольнитель, который будет без переключения между потоками
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
//        Во-первых, получите ссылку объект ImageCapture.
//        Если ссылка на объект явлется null, то выходим из функции.
//        Причина, по ссылка которой может быть bull,
//        если мы нажмем кнопку фото до того, как будет
//        настроен захват изображения.
//        Без оператора return приложение вылетит.
        val imageCapture = imageCapture ?: return

//        Затем создаем значение содержимого MediaStore для
//        хранения изображения. Используем временую метку,
//        чтобы отображаемое имя в MediaStore было уникальным.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

//        Создаем объект OutputFileOptions. В этом объекте мы
//        можем указать, каким должен быть результат. Мы хотим,
//        чтобы выходные данные сохранялись в MediaStore, чтобы
//        другие приложения могли их отображать, поэтому
//        добальем нашу запись MediaStore.
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

//        Вызываем takePicture() для объекта imageCapture.
//        Передаем outputOptions, исполнитель и callback сохранения изображения.
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
//                    В случае сбоя захвата изображения или сбоя сохранения
//                    изображения пишем в журнал инфорация об ошибке.
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
//                    Если захват удался, фотография была сделана успешно!
//                    После сохранения, уведомляем пользователя.
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun startCamera() {
//      Создаем экземпляр ProcessCameraProvider.
//      Это используется для привязки жизненного цикла камер к
//      владельцу жизненного цикла. Это устраняет необходимость
//      открывать и закрывать камеру, поскольку CameraX учитывает
//      жизненный цикл.
        //val mes = findViewById<TextView>(R.id.message)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)


//        Добавляем обработчик в cameraProviderFuture.
//        В качестве первого аргумета добавляем Runnable.
//        Рассмотрим его несколько позже
//        В качестве второго аргумента добавляем ContextCompat.getMainExecutor().
//        Это возвращает Executor(Исполнитель), который работает в основном потоке.
        cameraProviderFuture.addListener({

//            Добавляем ProcessCameraProvider. Это используется
//            для привязки жизненного цикла нашей камеры к
//            владельцу жизненного цикла в процессе приложения.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // получение View "viewFinder" из layout
            val viewFinder = findViewById<PreviewView>(R.id.viewFinder)

            // Preview - Объект, который обеспечивает поток предварительного просмотра
            // камеры для отображения на экране.
            // Инициализируем этот объект, вызываем build на нем,
            // получаем провайдер поверхности из viewFinder, а затем
            // устанавливаем его на превью (отображение данных с камеры).

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

//            Объект для захвата изображений
            imageCapture = ImageCapture.Builder().build()


            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                }


            // Выбираем основную (заднюю) камеру
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

//            Создаем блок try. Внутри этого блока проверяем,
//            что ничего не привязано к cameraProvider, а затем
//            привязываем наш cameraSelector и объект предварительного
//            просмотра к cameraProvider.
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,
//                    добавляем захват изображений в жизненный цикл
                    imageCapture,
                    imageAnalyzer
                )
                val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        // текущий коэффициент масштабирования камеры
                        val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 0F
                        // коэффициент масштабирования жестов
                        val delta = detector.scaleFactor
                        // Обновление коэффициента масштабирования камеры
                        Log.d(
                            "Scaling",
                            "$currentZoomRatio * $delta = ${currentZoomRatio * delta} "
                        )
                        camera.cameraControl.setZoomRatio(currentZoomRatio * delta)
                        return true
                    }
                }
                val scaleGestureDetector = ScaleGestureDetector(this, listener)
                viewFinder.setOnTouchListener { _, event ->
                    scaleGestureDetector.onTouchEvent(event)

                    if (event.action != MotionEvent.ACTION_UP) {
                        return@setOnTouchListener true
                    }

                    val factory = viewFinder.meteringPointFactory

                    val point = factory.createPoint(event.x, event.y)
                    val action = FocusMeteringAction.Builder(point).build()
                    camera.cameraControl.startFocusAndMetering(action)
                    return@setOnTouchListener true
                }
            } catch (exc: Exception) {
//                Есть несколько причин, по которым этот код
//                может дать сбой, например, если приложение больше
//                не находится в фокусе. Оберачиваем этот код в блок catch,
//                чтобы регистрировать в случае сбоя
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}