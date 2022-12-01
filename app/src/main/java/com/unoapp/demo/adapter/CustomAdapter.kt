package com.unoapp.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.unoapp.demo.R
import com.unoapp.demo.databinding.ListLayoutBinding
import com.unoapp.demo.model.Marvels
import com.unoapp.demo.utils.showLog

class CustomAdapter(val userList: ArrayList<Marvels.MarvelsItem>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: AdapterView.OnItemClickListener? = null
    private var listData: ArrayList<Marvels.MarvelsItem> = ArrayList()
    private var filteredListData: ArrayList<Marvels.MarvelsItem> = ArrayList()

    fun doRefresh(data: List<Marvels.MarvelsItem>) {
        listData.addAll(data)
        filteredListData.addAll(data)
        notifyDataSetChanged()
    }


    fun setOnItemClickListener(onItemClickListener: AdapterView.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }
    //this method is returning the view for each item in the list
 /*   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {

        val layoutBinding: ListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_layout, parent, false)
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
        return ViewHolder(v)
    }*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding: ListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_layout, parent, false)
        return ViewHolder(layoutBinding, this)
    }
    //this method is binding the data on the list
  /*  override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredListData[position]
        holder.populateItemRows(item, position, null)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Any>) {
        val item = filteredListData[position]
        holder.populateItemRows(item, position, payload)
    }
    override fun getItemCount(): Int {
        return filteredListData.size
    }

    /*interface OnChatItemClickListener {
        fun onClearChatClick(position: Int, broadcastChat: BroadcastTable)
        fun onDeleteBroadcastChatClick(position: Int, broadcastChat: BroadcastTable)
    }*/
    //this method is giving the size of the list
   /* override fun getItemCount(): Int {
        return userList.size
    }*/
 
    //the class is hodling the list view
    /*class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
 
        fun bindItems(marvelsItem: Marvels.MarvelsItem) {
            val textViewName = itemView.findViewById(R.id.textViewUsername) as TextView
            val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView
            textViewName.text = user.name
            textViewAddress.text = user.address
        }
    }*/

    inner class ViewHolder(private val layoutBinding: ListLayoutBinding, private val mAdapter: CustomAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
              /*  R.id.txt_clear_chat -> {
                    filteredListData[adapterPosition].let { listener.onClearChatClick(adapterPosition, it) }
                }
                R.id.txt_delete_broadcast -> {
                    mItemManger.closeAllItems()
                    filteredListData[adapterPosition].let { listener.onDeleteBroadcastChatClick(adapterPosition, it) }
                }
                R.id.content_layout -> {
                    mAdapter.onItemHolderClick(this)
                    closeAllItems() //close if any swipe layout is open
                }*/
            }
        }

        fun setData(obj: Marvels.MarvelsItem) {
            layoutBinding.txtChatTitle.text = obj.name
            /*layoutBinding.txtTime.text = DateTimeUtils.instance?.formatDateTime(obj.updatedAt, DateTimeUtils.DateFormats.yyyyMMddHHmmss.label)?.let {
                DateTimeUtils.instance?.getConversationTimestamp(
                    it.time
                )
            }*/
            //layoutBinding.txtChatMsg.text = obj.chats.last()?.messageText
            layoutBinding.txtChatMsg.text = obj.bio
            //layoutBinding.imgProfile.load(obj.broadcastIcon ?: "", false)
        }

        /**
         * populate rows
         *
         * @param holder
         * @param position
         */
        fun populateItemRows(obj: Marvels.MarvelsItem, position: Int, listPayload: List<Any>?) {


            if (listPayload == null || listPayload.isEmpty()) {
                setData(obj)
            } else {
                showLog("PAYLOAD :", Gson().toJson(listPayload))
                /* for (payload in listPayload) {

                 }*/
            }
          //  mItemManger.bind(layoutBinding.root, adapterPosition)

        }

        init {
            layoutBinding.contentLayout.setOnClickListener(this)
            //layoutBinding.txtClearChat.setOnClickListener(this)
            //layoutBinding.txtDeleteBroadcast.setOnClickListener(this)

        }
    }

}