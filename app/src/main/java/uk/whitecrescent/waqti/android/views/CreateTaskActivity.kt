package uk.whitecrescent.waqti.android.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_task.*
import uk.whitecrescent.waqti.R

class CreateTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        send_button.setOnClickListener {
            Snackbar.make(it,
                    "Here we will add the Task created then go back, this is why we should used " +
                            "Fragments not Activities! :/",
                    Snackbar.LENGTH_LONG
            ).show()
            //Caches.tasks.put(Task("${editText.text} ${System.currentTimeMillis()}"))
        }
    }
}
