package com.example.yuria.scheduler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleEditActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        // Realmインスタンスの作成
        realm = Realm.getDefaultInstance()

        // MainActivityでタップされたデータのid．取得できなかった場合（新規登録）は-1とする
        val scheduleId = intent?.getLongExtra("schedule_id", -1L)
        if (scheduleId != -1L) {
            // idフィールドが同じレコードを取得
            val schedule = realm.where<Schedule>().equalTo("id", scheduleId).findFirst()
            dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.date))
            titleEdit.setText(schedule?.title)
            detailEdit.setText(schedule?.detail)
            // 更新時の場合のみ削除ボタンを表示
            delete.visibility = View.VISIBLE
        } else {
            delete.visibility = View.INVISIBLE
        }

        // 保存ボタンが押された時の処理
        save.setOnClickListener {
            when (scheduleId) {
                // 新規登録の場合
                -1L -> {
                    // トランザクション処理でデータベースへの書き込み
                    realm.executeTransaction {
                        // Scheduleのidフィールドの最大値を取得し＋１したものを新idとする
                        val maxId = realm.where<Schedule>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1 // nullの場合はLong値0に＋１したものを使用
                        // データを追加するメソッド
                        val schedule = realm.createObject<Schedule>(nextId) // 引数にはPrimaryKeyを使用
                        dateEdit.text.toString().toDate("yyyy/MM/dd")?.let {
                            schedule.date = it
                        }
                        schedule.title = titleEdit.text.toString()
                        schedule.detail = detailEdit.text.toString()
                    }
                    alert("追加しました") {
                        yesButton { finish() } // MainActivityに戻る
                    }.show()
                }
                // 更新の場合
                else -> {
                    realm.executeTransaction {
                        val schedule = realm.where<Schedule>().equalTo("id", scheduleId).findFirst()
                        dateEdit.text.toString().toDate("yyyy/MM/dd")?.let {
                            schedule?.date = it
                        }
                        schedule?.title = titleEdit.text.toString()
                        schedule?.detail = detailEdit.text.toString()
                    }
                    alert("修正しました") {
                        yesButton { finish() } // MainActivityに戻る
                    }.show()
                }
            }
        }

        // 削除ボタンが押された時の処理
        delete.setOnClickListener {
            realm.executeTransaction {
                // idフィールドの同じレコードを削除
                realm.where<Schedule>().equalTo("id", scheduleId)?.findFirst()?.deleteFromRealm()
            }
            alert("削除しました") {
                yesButton { finish() }
            }.show()
        }
    }

    // データベースを終了するメソッド
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    // String -> Dateの拡張関数
    fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        val sdFormat = try {
            SimpleDateFormat(pattern)
        } catch (e:IllegalArgumentException) {
            null
        }
        val date = sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException) {
                null
            }
        }
        return date
    }
}
