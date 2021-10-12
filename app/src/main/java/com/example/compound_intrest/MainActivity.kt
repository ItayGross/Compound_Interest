package com.example.compound_intrest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.compound_intrest.databinding.ActivityMainBinding
import kotlin.math.pow
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var didPopup = false

        fun doPopup(view: View) {

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            // Set an elevation for the popup window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10.0F
            }


            // If API level 23 or higher then execute the code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.END
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.END
                popupWindow.exitTransition = slideOut
            }

            binding.root.setOnClickListener {
                popupWindow.dismiss()
                didPopup = false
            }

            // Finally, show the popup window on app
            TransitionManager.beginDelayedTransition(binding.root)
            popupWindow.showAtLocation(
                binding.root, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }

        fun calculateMoney(
            initialValue: Int,
            years: Double,
            interestRate: Double,
            Contribute: Int
        ): Double {
            val yearlyInterest: Double =
                1 + (interestRate / 100)
            val monthlyInterest: Double =
                yearlyInterest.pow(1.toDouble() / 12)
            var combinedValue =
                initialValue.toDouble()
            val spinner: Spinner = findViewById(R.id.spinner)
            if (spinner.selectedItem.equals("Monthly")) {
                repeat((years * 12).toInt()) {
                    combinedValue =
                        (combinedValue * monthlyInterest + Contribute.toDouble()) // מוסיף כל חודש את התוספת החודשית ומחשב ריבית
                }
            } else {
                repeat(years.toInt()) {
                    combinedValue = (combinedValue * yearlyInterest + Contribute.toDouble())
                }
            }
            return combinedValue
        }

        fun inputError() {
            if (binding.initialValue.text.toString().isEmpty()) {
                binding.textView1.background = getDrawable(R.drawable.back)
            }
            if (binding.years.text.toString().isEmpty() || binding.years.text.toString()
                    .toDouble() == 0.0
            ) {
                binding.textView2.background = getDrawable(R.drawable.back)
            }
            if (binding.interestRate.text.toString().isEmpty()) {
                binding.textView3.background = getDrawable(R.drawable.back)
            }
        }

        binding.monthlyContribute.doAfterTextChanged {
            when (it.toString()) {
                "-" -> binding.monthlyChanged.text = "-"
                "" -> binding.monthlyChanged.text = ""
                else -> {
                    val newText = "%,d".format(binding.monthlyContribute.text.toString().toInt())
                    binding.monthlyChanged.text = newText
                }
            }
        }

        binding.initialValue.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                val newText = "%,d".format(it.toString().toInt())
                binding.initialValueChanged.text = newText
            } else {
                binding.initialValueChanged.text = ""
            }
        }

        binding.calcButton.setOnClickListener {
            with(binding) {
                textView1.background = getDrawable(R.color.white)
                textView2.background = getDrawable(R.color.white)
                textView3.background = getDrawable(R.color.white)
            }
            if (binding.initialValue.text.toString()
                    .isNotEmpty() && binding.years.text.toString()
                    .isNotEmpty() && binding.interestRate.text.toString()
                    .isNotEmpty()
            ) {
                val initialValue = binding.initialValue.text.toString().toInt()
                val years = binding.years.text.toString().toDouble()
                val interestRate = binding.interestRate.text.toString().toDouble()
                val monthlyContribute: Int =
                    if (binding.monthlyContribute.text.toString().isEmpty()) {
                        0
                    } else {
                        binding.monthlyContribute.text.toString().toInt()
                    }
                if (years > 0) {
                    val money = calculateMoney(initialValue, years, interestRate, monthlyContribute)
                    val spinner: Spinner = findViewById(R.id.spinner)
                    val earnedInterest: Int
                    val moneyContribute: Int
                    if (spinner.selectedItem.equals("Monthly")) {
                        earnedInterest =
                            money.roundToInt() - initialValue - (monthlyContribute * (years * 12)).roundToInt()
                        moneyContribute =
                            (initialValue + monthlyContribute * (years * 12)).roundToInt()
                    } else {
                        earnedInterest =
                            money.roundToInt() - initialValue - (monthlyContribute * years).roundToInt()
                        moneyContribute = (initialValue + monthlyContribute * years).roundToInt()
                    }
                    binding.earnedMoney.text =
                        "Accumulated Earnings: " + "%,d".format(money.roundToInt()) + "$" + "\nEarned Interest: " + "%,d".format(
                            earnedInterest
                        ) + "$" + "\nMoney Contributed: " + "%,d".format(moneyContribute) + "$"
                    with(binding) {
                        earnedMoney.setTextColor(Color.parseColor("#000000"))
                        resetButton.visibility = VISIBLE
                    }
                } else {
                    inputError()
                    binding.earnedMoney.text = "Enter Valid Numbers"
                    binding.earnedMoney.setTextColor(Color.parseColor("#FF0000"))
                }
            } else {
                inputError()
                binding.earnedMoney.text = "Fill All Required Boxes"
                binding.earnedMoney.setTextColor(Color.parseColor("#FF0000"))
            }
        }

        binding.resetButton.setOnClickListener {
            with(binding) {
                textView1.background = getDrawable(R.color.white)
                textView2.background = getDrawable(R.color.white)
                textView3.background = getDrawable(R.color.white)
                initialValue.setText("")
                years.setText("")
                interestRate.setText("")
                monthlyContribute.setText("")
                earnedMoney.text = ""
                resetButton.visibility = GONE
                initialValue.requestFocus()
            }
        }

        var spinner: Spinner = findViewById(R.id.spinner)
        val monthlyYearly = arrayOf<String?>("Monthly", "Yearly")
        val arrayAdapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, R.layout.spinner_text, monthlyYearly)
        spinner.adapter = arrayAdapter

        binding.initialValuePopup?.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.initial_value_popup, null)
            if (!didPopup) {
                doPopup(view)
                didPopup = true
            }
        }

        binding.contributePopup?.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.contribue_popup, null)
            if (!didPopup) {
                doPopup(view)
                didPopup = true
            }
        }

        binding.yearsPopup?.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.years_popup, null)
            if (!didPopup) {
                doPopup(view)
                didPopup = true
            }
        }

        binding.interestRatePopup?.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.interest_rate_popup, null)
            if (!didPopup) {
                doPopup(view)
                didPopup = true
            }
        }

        binding.popUpButton?.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.popup, null)
            if (!didPopup) {
                doPopup(view)
                didPopup = true
            }
        }
    }
}