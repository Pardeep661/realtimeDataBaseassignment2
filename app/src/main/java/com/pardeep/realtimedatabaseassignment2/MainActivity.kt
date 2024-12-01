package com.pardeep.realtimedatabaseassignment2

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pardeep.realtimedatabaseassignment2.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() ,RecyclerInterface {
    var binding: ActivityMainBinding? = null
    var timeArray = arrayListOf<DataClass>()
    var myAdp = MyAdp(timeArray, this)
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var databaseReference: DatabaseReference
    var timeFormat = SimpleDateFormat("HH:mm:ss a", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        databaseReference = FirebaseDatabase.getInstance().getReference("Time related data")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // --------------------- realtime crud- ----------------------
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val timeModel : DataClass ? = snapshot.getValue(DataClass::class.java)
                timeModel?.id = snapshot.key

                if (timeModel!=null){
                    timeArray.add(timeModel)
                    myAdp.notifyDataSetChanged()
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val timeModel: DataClass? = snapshot.getValue(DataClass::class.java)
                timeModel?.id = snapshot.key
                if (timeModel != null) {


                    timeArray.forEachIndexed { index, dataClassModel ->
                        if (dataClassModel.id == timeModel.id) {
                            timeArray[index] = timeModel
                            myAdp.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val timeModel : DataClass ? = snapshot.getValue(DataClass::class.java)
                timeModel?.id = snapshot.key

                if (timeModel !=null){
                    timeArray.remove(timeModel)
                    myAdp.notifyDataSetChanged()
                }

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        // --------------------- realtime crud- ----------------------


        //------------------fab ----------------------------
        binding?.fab?.setOnClickListener {

            TimePickerDialog(
                this@MainActivity,
                { _, hour, minute ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    val formatedTime = timeFormat.format(calendar.time)
                    val key = databaseReference.push().key
                    val addData = DataClass(id = key.toString(), time = formatedTime)
                    databaseReference.child(key.toString()).setValue(addData)



                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),

                false
            ).show()
        }

        //------------------fab ----------------------------

        binding?.recyclerView?.adapter = myAdp
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.recyclerView?.layoutManager = linearLayoutManager
    }

    override fun onItemClick(position: Int) {

        AlertDialog.Builder(this).apply {
            setTitle("Do you want to delete time : ${timeArray[position].time}")
            setPositiveButton("Update") { _, _ ->
                updateTime(position, timeArray[position].time.toString())
            }
            setNegativeButton("Delete") { _, _ ->
                deleteTime(position)
            }
        }.show()
    }

    private fun deleteTime(position: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Do you want to delete time : ${timeArray[position].time}")
            setPositiveButton("Yes") { _, _ ->
                databaseReference.child(timeArray[position].id.toString()).removeValue()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    fun updateTime(position: Int, time: String) {
        val calendar = Calendar.getInstance()
        val parsedTime = timeFormat.parse(time)
        if (parsedTime != null) {
            calendar.time = parsedTime
        }
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                val formatedTime = timeFormat.format(calendar.time)
                val selectedKey = timeArray[position].id
                val updateData = DataClass(selectedKey , formatedTime)
                val mapData = updateData.toMap()
                databaseReference.child(selectedKey.toString()).updateChildren(mapData)
            },currentHour,currentMinute,


            false
        ).show()
    }
}

