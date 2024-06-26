package com.pro.foodorder.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.foodorder.ControllerApplication
import com.pro.foodorder.R
import com.pro.foodorder.activity.AdminMainActivity
import com.pro.foodorder.adapter.FeedbackAdapter
import com.pro.foodorder.databinding.FragmentAdminFeedbackBinding
import com.pro.foodorder.fragment.BaseFragment
import com.pro.foodorder.model.Feedback
import java.util.*

class AdminFeedbackFragment : BaseFragment() {

    private var mFragmentAdminFeedbackBinding: FragmentAdminFeedbackBinding? = null
    private var mListFeedback: MutableList<Feedback>? = null
    private var mFeedbackAdapter: FeedbackAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mFragmentAdminFeedbackBinding = FragmentAdminFeedbackBinding.inflate(inflater, container, false)
        initView()
        getListFeedback()
        return mFragmentAdminFeedbackBinding!!.root
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as AdminMainActivity?)!!.setToolBar(getString(R.string.feedback))
        }
    }

    private fun initView() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentAdminFeedbackBinding!!.rcvFeedback.layoutManager = linearLayoutManager
    }

    fun getListFeedback() {
        if (activity == null) {
            return
        }
        ControllerApplication[activity!!].feedbackDatabaseReference
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (mListFeedback != null) {
                            mListFeedback!!.clear()
                        } else {
                            mListFeedback = ArrayList()
                        }
                        for (dataSnapshot in snapshot.children) {
                            val feedback = dataSnapshot.getValue(Feedback::class.java)
                            if (feedback != null) {
                                mListFeedback!!.add(0, feedback)
                            }
                        }
                        mFeedbackAdapter = FeedbackAdapter(mListFeedback)
                        mFragmentAdminFeedbackBinding!!.rcvFeedback.adapter = mFeedbackAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
    }
}