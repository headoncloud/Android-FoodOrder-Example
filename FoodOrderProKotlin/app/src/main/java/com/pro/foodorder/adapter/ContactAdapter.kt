package com.pro.foodorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pro.foodorder.R
import com.pro.foodorder.adapter.ContactAdapter.ContactViewHolder
import com.pro.foodorder.constant.GlobalFunction.onClickOpenFacebook
import com.pro.foodorder.constant.GlobalFunction.onClickOpenGmail
import com.pro.foodorder.constant.GlobalFunction.onClickOpenSkype
import com.pro.foodorder.constant.GlobalFunction.onClickOpenYoutubeChannel
import com.pro.foodorder.constant.GlobalFunction.onClickOpenZalo
import com.pro.foodorder.databinding.ItemContactBinding
import com.pro.foodorder.model.Contact

class ContactAdapter(private var context: Context?, private val listContact: List<Contact>?,
                     private val iCallPhone: ICallPhone) : RecyclerView.Adapter<ContactViewHolder>() {

    interface ICallPhone {
        fun onClickCallPhone()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemContactBinding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(itemContactBinding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = listContact!![position]
        holder.mItemContactBinding.imgContact.setImageResource(contact.image)
        when (contact.id) {
            Contact.FACEBOOK -> holder.mItemContactBinding.tvContact.text = context!!.getString(R.string.label_facebook)
            Contact.HOTLINE -> holder.mItemContactBinding.tvContact.text = context!!.getString(R.string.label_call)
            Contact.GMAIL -> holder.mItemContactBinding.tvContact.text = context!!.getString(R.string.label_gmail)
            Contact.SKYPE -> holder.mItemContactBinding.tvContact.text = context!!.getString(R.string.label_skype)
            Contact.YOUTUBE -> holder.mItemContactBinding.tvContact.text = context!!.getString(R.string.label_youtube)
            Contact.ZALO -> holder.mItemContactBinding.tvContact.text = context!!.getString(R.string.label_zalo)
        }
        holder.mItemContactBinding.layoutItem.setOnClickListener {
            when (contact.id) {
                Contact.FACEBOOK -> onClickOpenFacebook(context!!)
                Contact.HOTLINE -> iCallPhone.onClickCallPhone()
                Contact.GMAIL -> onClickOpenGmail(context!!)
                Contact.SKYPE -> onClickOpenSkype(context!!)
                Contact.YOUTUBE -> onClickOpenYoutubeChannel(context!!)
                Contact.ZALO -> onClickOpenZalo(context!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return listContact?.size ?: 0
    }

    fun release() {
        context = null
    }

    class ContactViewHolder(val mItemContactBinding: ItemContactBinding) : RecyclerView.ViewHolder(mItemContactBinding.root)
}