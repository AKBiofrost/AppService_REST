package com.portafolio.servicesegundplan;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Aquí va la lógica que quieres ejecutar en segundo plano
        // Por ejemplo, podrías hacer una solicitud HTTP, procesar datos, etc.

        // Simulamos una tarea larga
        try {
            Thread.sleep(5000); // Simula una tarea que tarda 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure(); // Si falla, retornamos Result.failure()
        }

        // Si la tarea se completó exitosamente, retornamos Result.success()
        return Result.success();
    }
}
