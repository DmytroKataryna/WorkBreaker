package kataryna.app.workmanager.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kataryna.app.workmanager.R
import kataryna.app.workmanager.ui.tasks.TasksFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TasksFragment.newInstance())
                .commitNow()
        }
    }
}