package com.androidhuman.example.simplecontacts

import com.androidhuman.example.simplecontacts.model.Person

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var rvPeople: RecyclerView

    lateinit var tvEmpty: TextView

   // lateinit var peopleAdapter: PeopleAdapter

    val peopleAdapter by lazy { PeopleAdapter()}

    val realm by lazy { Realm.getDefaultInstance()}

    //lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPeople = findViewById(R.id.rv_activity_main) as RecyclerView
        tvEmpty = findViewById(R.id.tv_activity_main) as TextView

        rv_activity_main.layoutManager = LinearLayoutManager(this)
        rv_activity_main.adapter = peopleAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_main_add == item.itemId) {
            startActivityForResult(
                    Intent(this, EditActivity::class.java), REQUEST_CODE)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        queryPeople()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (RESULT_OK == resultCode && REQUEST_CODE == requestCode) {
            queryPeople()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            cleanUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanUp()
    }

    private fun cleanUp() {
        if (!realm.isClosed) {
            realm.close()
        }
    }

    private fun queryPeople() {
        realm.where(Person::class.java).findAllAsync()
                .addChangeListener { result ->
                    if (result.isLoaded) {
                        tvEmpty.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE

                        peopleAdapter.setPeople(result)
                        peopleAdapter.notifyDataSetChanged()

                        result.removeAllChangeListeners()
                    }
                }
    }
}
