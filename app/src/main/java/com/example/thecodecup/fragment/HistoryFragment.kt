package com.example.thecodecup.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.R
import com.example.thecodecup.adapter.OrderAdapter
import com.example.thecodecup.data.AppDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var adapter: OrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        adapter = OrderAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val db = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            db.orderDao().getCompletedOrders().collect {
                adapter.updateList(it)
            }
        }
        return view
    }
}
