package com.pro.foodorder.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.pro.foodorder.ControllerApplication
import com.pro.foodorder.R
import com.pro.foodorder.adapter.OrderAdapter
import com.pro.foodorder.databinding.ActivityOrderHistoryBinding
import com.pro.foodorder.model.Order
import com.pro.foodorder.prefs.DataStoreManager.Companion.user
import java.util.*

class OrderHistoryActivity : BaseActivity() {

    private var mActivityOrderHistoryBinding: ActivityOrderHistoryBinding? = null
    private var mListOrder: MutableList<Order>? = null
    private var mOrderAdapter: OrderAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityOrderHistoryBinding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(mActivityOrderHistoryBinding!!.root)
        initToolbar()
        initView()
        getListOrders()


    }

    private fun initToolbar() {
        mActivityOrderHistoryBinding!!.toolbar.imgBack.visibility = View.VISIBLE
        mActivityOrderHistoryBinding!!.toolbar.imgCart.visibility = View.GONE
        mActivityOrderHistoryBinding!!.toolbar.tvTitle.text = getString(R.string.order_history)
        mActivityOrderHistoryBinding!!.toolbar.imgBack.setOnClickListener { onBackPressed() }
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityOrderHistoryBinding!!.rcvOrderHistory.layoutManager = linearLayoutManager
    }

    private fun getListOrders() {
        ControllerApplication[this].bookingDatabaseReference
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (mListOrder != null) {
                            mListOrder!!.clear()
                        } else {
                            mListOrder = ArrayList()
                        }
                        for (dataSnapshot in snapshot.children) {
                            val order = dataSnapshot.getValue(Order::class.java)
                            if (order != null) {
                                val strEmail = user!!.email
                                if (strEmail.equals(order.email, ignoreCase = true)) {
                                    mListOrder!!.add(0, order)
                                }
                            }
                        }
                        mOrderAdapter = OrderAdapter(this@OrderHistoryActivity, mListOrder)
                        mActivityOrderHistoryBinding!!.rcvOrderHistory.adapter = mOrderAdapter
                    }


                    override fun onCancelled(error: DatabaseError) {}
                })
    }

     fun discardOrder(order: Order?){
         // Kiểm tra nếu order là null thì không thực hiện gì
         if (order == null) return

        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này không?")
            .setPositiveButton(getString(R.string.action_ok)) { _: DialogInterface?, _: Int ->
                // Kiểm tra nếu Activity đã bị hủy thì không thực hiện gì
                if (isFinishing) {
                    return@setPositiveButton
                }

                if (this == null) {
                    return@setPositiveButton
                }
                ControllerApplication[this].bookingDatabaseReference
                    .child(order!!.id.toString()).removeValue { _: DatabaseError?, _: DatabaseReference? ->
                        Toast.makeText(this,
                            getString(R.string.msg_discard_order_success), Toast.LENGTH_SHORT).show()

                        // Cập nhật lại danh sách hiển thị sau khi xóa
                        mListOrder?.remove(order)
                        mOrderAdapter?.notifyDataSetChanged()
                    }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mOrderAdapter != null) {
            mOrderAdapter!!.release()
        }
    }
}