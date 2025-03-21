package com.example.chair

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val translateData = TranslateData(
            "以前一直是在布局里直接设置ConstraintLayout的约束，前几天遇到了一个有点特殊的需求，需要动态设置布局，xml布局使用的ConstraintLayout，所以就立马恶补动态设置所以就立",
            "翻译", false
        )

        findViewById<TranslateLayout>(R.id.layout_translate).setupTranslate(translateData)
    }
}