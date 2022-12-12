package android.libs.demo.shadow

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import sing.shadow.ShadowView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val shadowView: ShadowView = findViewById(R.id.shadow_view)
        shadowView.setOnClickListener {
            Toast.makeText(this@MainActivity, "1", Toast.LENGTH_SHORT).show()
        }

        val viewRecyclerView: RecyclerView = findViewById(R.id.view_recycler)
        viewRecyclerView.adapter = MyAdapter(shadowView)
    }
}