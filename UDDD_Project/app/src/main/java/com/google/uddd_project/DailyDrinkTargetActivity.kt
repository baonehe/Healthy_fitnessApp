package com.google.uddd_project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.uddd_project.helpers.AlarmHelper
import com.google.uddd_project.helpers.SqliteHelper
import com.google.uddd_project.utils.AppUtils
import kotlinx.android.synthetic.main.activity_daily_drink_target.*


class DailyDrinkTargetActivity : AppCompatActivity() {

    private var totalIntake: Int = 0
    private var inTook: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private var selectedOption: Int? = null
    private var snackbar: Snackbar? = null
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_drink_target)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Daily Drink Target"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        sqliteHelper = SqliteHelper(this)

        totalIntake = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

//        if (sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true)) {
//            startActivity(Intent(this, WalkThroughActivity::class.java))
//            finish()
//        } else

        if (totalIntake <= 0) {
            startActivity(Intent(this, ProvideInforToCustom::class.java))
            finish()
        }

        dateNow = AppUtils.getCurrentDate()!!

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateValues() {
        totalIntake = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

        inTook = sqliteHelper.getIntook(dateNow)

        setWaterLevel(inTook, totalIntake)
    }

    override fun onStart() {
        super.onStart()

        val outValue = TypedValue()
        applicationContext.theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValue,
                true
        )

        notificStatus = sharedPref.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        val alarm = AlarmHelper()
        if (!alarm.checkAlarm(this) && notificStatus) {
            btnNotific.setImageDrawable(getDrawable(R.drawable.alarm))
            alarm.setAlarm(
                    this,
                    sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
            )
        }

        if (notificStatus) {
            btnNotific.setImageDrawable(getDrawable(R.drawable.alarm))
        } else {
            btnNotific.setImageDrawable(getDrawable(R.drawable.alarm_disabled))
        }

        sqliteHelper.addAll(dateNow, 0, totalIntake)

        updateValues()

        btn_settingInfo.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment(this)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        btnAddWater.setOnClickListener {
            if (selectedOption != null) {
                if ((inTook * 100 / totalIntake) <= 140) {
                    if (sqliteHelper.addIntook(dateNow, selectedOption!!) > 0) {
                        inTook += selectedOption!!
                        setWaterLevel(inTook, totalIntake)

                        Snackbar.make(it, "Your water intake was saved...!!", Snackbar.LENGTH_SHORT)
                                .show()

                    }
                } else {
                    Snackbar.make(it, "You already achieved the goal", Snackbar.LENGTH_SHORT).show()
                }
                selectedOption = null
                tvCustom.text = "Custom"
                op50ml.background = getDrawable(R.drawable.button_white)
                op100ml.background = getDrawable(R.drawable.button_white)
                op150ml.background = getDrawable(R.drawable.button_white)
                op200ml.background = getDrawable(R.drawable.button_white)
                op250ml.background = getDrawable(R.drawable.button_white)
                opCustom.background = getDrawable(R.drawable.button_white)
            } else {
                YoYo.with(Techniques.Shake)
                        .duration(700)
//                        .playOn(cardView)
                Snackbar.make(it, "Please select an option", Snackbar.LENGTH_SHORT).show()
            }
        }

        btnNotific.setOnClickListener {
            notificStatus = !notificStatus
            sharedPref.edit().putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, notificStatus).apply()
            if (notificStatus) {
                btnNotific.setImageDrawable(getDrawable(R.drawable.alarm))
                Snackbar.make(it, "Notification Enabled..", Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                        this,
                        sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                )
            } else {
                btnNotific.setImageDrawable(getDrawable(R.drawable.alarm_disabled))
                Snackbar.make(it, "Notification Disabled..", Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(this)
            }
        }

        btnStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }


        op50ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 50
            op50ml.background = getDrawable(R.drawable.background_details_medication)
            op100ml.background = getDrawable(R.drawable.button_white)
            op150ml.background = getDrawable(R.drawable.button_white)
            op200ml.background = getDrawable(R.drawable.button_white)
            op250ml.background = getDrawable(R.drawable.button_white)
            opCustom.background = getDrawable(R.drawable.button_white)
        }

        op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 100
            op50ml.background = getDrawable(R.drawable.button_white)
            op100ml.background = getDrawable(R.drawable.background_details_medication)
            op150ml.background = getDrawable(R.drawable.button_white)
            op200ml.background = getDrawable(R.drawable.button_white)
            op250ml.background = getDrawable(R.drawable.button_white)
            opCustom.background = getDrawable(R.drawable.button_white)

        }

        op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 150
            op50ml.background = getDrawable(R.drawable.button_white)
            op100ml.background = getDrawable(R.drawable.button_white)
            op150ml.background = getDrawable(R.drawable.background_details_medication)
            op200ml.background = getDrawable(R.drawable.button_white)
            op250ml.background = getDrawable(R.drawable.button_white)
            opCustom.background = getDrawable(R.drawable.button_white)

        }

        op200ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 200
            op50ml.background = getDrawable(R.drawable.button_white)
            op100ml.background = getDrawable(R.drawable.button_white)
            op150ml.background = getDrawable(R.drawable.button_white)
            op200ml.background = getDrawable(R.drawable.background_details_medication)
            op250ml.background = getDrawable(R.drawable.button_white)
            opCustom.background = getDrawable(R.drawable.button_white)

        }

        op250ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 250
            op50ml.background = getDrawable(R.drawable.button_white)
            op100ml.background = getDrawable(R.drawable.button_white)
            op150ml.background = getDrawable(R.drawable.button_white)
            op200ml.background = getDrawable(R.drawable.button_white)
            op250ml.background = getDrawable(R.drawable.background_details_medication)
            opCustom.background = getDrawable(R.drawable.button_white)

        }

        opCustom.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }

            val li = LayoutInflater.from(this)
            val promptsView = li.inflate(R.layout.custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                    .findViewById(R.id.etCustomInput) as TextInputLayout

            alertDialogBuilder.setPositiveButton("OK") { dialog, id ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    tvCustom.text = "${inputText} ml"
                    selectedOption = inputText.toInt()
                }
            }.setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            op50ml.background = getDrawable(R.drawable.button_white)
            op100ml.background = getDrawable(R.drawable.button_white)
            op150ml.background = getDrawable(R.drawable.button_white)
            op200ml.background = getDrawable(R.drawable.button_white)
            op250ml.background = getDrawable(R.drawable.button_white)
            opCustom.background = getDrawable(R.drawable.background_details_medication)
        }
    }


    fun setWaterLevel(inTook: Int, totalIntake: Int) {
        YoYo.with(Techniques.SlideInDown)
                .duration(500)
                .playOn(tvIntook)
        tvIntook.text = "$inTook ml"
        tvTotalIntake.text = "$totalIntake ml"
        val progress = ((inTook / totalIntake.toFloat()) * 100).toInt()
        YoYo.with(Techniques.Pulse)
                .duration(500)
                .playOn(waterLevelView)
        waterLevelView.setProgress(100-progress)
        if ((inTook * 100 / totalIntake) > 140) {
            Snackbar.make(mainActivityDailyDrink, "You achieved the goal", Snackbar.LENGTH_SHORT)
                    .show()
        }
    }

//    override fun onBackPressed() {
//
//        startActivity(Intent(this, HomeFragment::class.java))
//        finish()
//
////        if (doubleBackToExitPressedOnce) {
////            super.onBackPressed()
////            return
////        }
////
////        this.doubleBackToExitPressedOnce = true
////        Snackbar.make(
////            this.window.decorView.findViewById(android.R.id.content),
////            "Please click BACK again to exit",
////            Snackbar.LENGTH_SHORT
////        ).show()
//
//        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
//    }

}
