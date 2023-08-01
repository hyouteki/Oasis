package com.hyouteki.oasis.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hyouteki.oasis.R
import com.hyouteki.oasis.comms.AdapterComms
import com.hyouteki.oasis.models.Confession
import com.hyouteki.oasis.viewmodels.OasisViewModel


class ConfessionAdapter(
    private val listener: AdapterComms,
    options: FirestoreRecyclerOptions<Confession>,
    private val context: Context
) :
    FirestoreRecyclerAdapter<Confession, ConfessionAdapter.ViewModel>(
        options
    ) {
    inner class ViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val confessionCard: CardView = view.findViewById(R.id.confession_card)
        val addressedTo: TextView = view.findViewById(R.id.addressed_to)
        val confession: TextView = view.findViewById(R.id.confession)
        val everything: ConstraintLayout = view.findViewById(R.id.everything)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        return ViewModel(
            LayoutInflater
                .from(context)
                .inflate(R.layout.confession_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int, model: Confession) {
        holder.confessionCard.visibility = View.VISIBLE
        OasisViewModel.selectConfession(model.id).addOnSuccessListener { doc ->
            doc.toObject(Confession::class.java)?.let { confession ->
                holder.addressedTo.text = confession.addressedTo
                holder.confession.text = confession.confession
            }
        }
        holder.everything.setOnLongClickListener {
            listener.onConfessionLongClicked(model)
            true
        }
    }

    override fun getItemViewType(position: Int) = position
}





