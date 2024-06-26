package com.pro.foodorder.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.foodorder.ControllerApplication
import com.pro.foodorder.R
import com.pro.foodorder.adapter.RevenueAdapter
import com.pro.foodorder.constant.Constant
import com.pro.foodorder.constant.GlobalFunction.showDatePicker
import com.pro.foodorder.databinding.ActivityAdminReportBinding
import com.pro.foodorder.listener.IGetDateListener
import com.pro.foodorder.listener.IOnSingleClickListener
import com.pro.foodorder.model.Order
import com.pro.foodorder.utils.DateTimeUtils.convertDate2ToTimeStamp
import com.pro.foodorder.utils.DateTimeUtils.convertTimeStampToDate_2
import com.pro.foodorder.utils.StringUtil.isEmpty
import java.util.*

class AdminReportActivity : AppCompatActivity() {

    private var mActivityAdminReportBinding: ActivityAdminReportBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAdminReportBinding = ActivityAdminReportBinding.inflate(layoutInflater)
        setContentView(mActivityAdminReportBinding!!.root)
        initToolbar()
        initListener()
        getListRevenue()
    }

    private fun initToolbar() {
        mActivityAdminReportBinding!!.toolbar.imgBack.visibility = View.VISIBLE
        mActivityAdminReportBinding!!.toolbar.imgCart.visibility = View.GONE
        mActivityAdminReportBinding!!.toolbar.tvTitle.text = getString(R.string.revenue)
        mActivityAdminReportBinding!!.toolbar.imgBack.setOnClickListener { onBackPressed() }
    }

    private fun initListener() {
        mActivityAdminReportBinding!!.tvDateFrom.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(this@AdminReportActivity,
                        mActivityAdminReportBinding!!.tvDateFrom.text.toString(), object : IGetDateListener {
                    override fun getDate(date: String?) {
                        mActivityAdminReportBinding!!.tvDateFrom.text = date
                        getListRevenue()
                    }
                })
            }
        })
        mActivityAdminReportBinding!!.tvDateTo.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(this@AdminReportActivity,
                        mActivityAdminReportBinding!!.tvDateTo.text.toString(), object : IGetDateListener {
                    override fun getDate(date: String?) {
                        mActivityAdminReportBinding!!.tvDateTo.text = date
                        getListRevenue()
                    }
                })
            }
        })
    }

    private fun getListRevenue() {
        ControllerApplication[this].bookingDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<Order> = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Order::class.java)!!
                    if (canAddOrder(order)) {
                        list.add(0, order)
                    }
                }
                handleDataHistories(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun canAddOrder(order: Order?): Boolean {
        if (order == null) {
            return false
        }
        if (!order.isCompleted) {
            return false
        }
        val strDateFrom = mActivityAdminReportBinding!!.tvDateFrom.text.toString()
        val strDateTo = mActivityAdminReportBinding!!.tvDateTo.text.toString()
        if (isEmpty(strDateFrom) && isEmpty(strDateTo)) {
            return true
        }
        val strDateOrder = convertTimeStampToDate_2(order.id)
        val longOrder = convertDate2ToTimeStamp(strDateOrder).toLong()
        if (isEmpty(strDateFrom) && !isEmpty(strDateTo)) {
            val longDateTo = convertDate2ToTimeStamp(strDateTo).toLong()
            return longOrder <= longDateTo
        }
        if (!isEmpty(strDateFrom) && isEmpty(strDateTo)) {
            val longDateFrom = convertDate2ToTimeStamp(strDateFrom).toLong()
            return longOrder >= longDateFrom
        }
        val longDateTo = convertDate2ToTimeStamp(strDateTo).toLong()
        val longDateFrom = convertDate2ToTimeStamp(strDateFrom).toLong()
        return longOrder in longDateFrom..longDateTo
    }

    private fun handleDataHistories(list: List<Order>?) {
        if (list == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityAdminReportBinding!!.rcvOrderHistory.layoutManager = linearLayoutManager
        val revenueAdapter = RevenueAdapter(list)
        mActivityAdminReportBinding!!.rcvOrderHistory.adapter = revenueAdapter

        // Calculate total
        val strTotalValue: String = "" + getTotalValues(list) + Constant.CURRENCY
        mActivityAdminReportBinding!!.tvTotalValue.text = strTotalValue
    }

    private fun getTotalValues(list: List<Order>?): Int {
        if (list == null || list.isEmpty()) {
            return 0
        }
        var total = 0
        for (order in list) {
            total += order.amount
        }
        return total
    }
}