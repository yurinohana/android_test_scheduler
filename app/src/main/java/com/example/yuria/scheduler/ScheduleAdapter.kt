package com.example.yuria.scheduler

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class ScheduleAdapter(data: OrderedRealmCollection<Schedule>?) : RealmBaseAdapter<Schedule>(data) {
    inner class ViewHolder(cell: View) {
        val date = cell.findViewById<TextView>(android.R.id.text1)
        val title = cell.findViewById<TextView>(android.R.id.text2)
    }
    // 表示するセルを返すメソッド
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        // viewオブジェクトを保持する
        val viewHolder :ViewHolder

        // スクロールして画面から見えなくなったセルビューを再利用した場合，convertViewが存在して再利用するのでelse
        // 新規セルビューの場合はnullで新しく作成される
        when (convertView) {
            null -> {
                // xmlファイルからビューを生成するLayoutInflaterクラスのインスタンス作成
                val inflater = LayoutInflater.from(parent?.context)
                // xmlファイルから画面レイアウトを作成
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
                // list_item_2に配置されているtext1,text2のテキストビューの内容をメンバ変数として保持
                viewHolder = ViewHolder(view)
                // ビューにviewHolderをタグづけ
                view.tag = viewHolder
            }
            else -> {
                view = convertView
                // ViewHolderかたtagプロパティを取り出す
                viewHolder = view.tag as ViewHolder
            }
        }

        // データのリストの表示
        adapterData?.run {
            // position番目のデータの取り出し
            val schedule = get(position)
            // テキストビューの設定
            viewHolder.date.text = DateFormat.format("yyyy/MM/dd", schedule.date)
            viewHolder.title.text = schedule.title
        }
        return  view
    }
}