package com.nongmah.noteapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nongmah.noteapp.R
import com.nongmah.noteapp.data.local.entities.Note
import com.nongmah.noteapp.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var onItemClickListener: ((Note) -> Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var notes: List<Note>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(note)
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setOnItemClickListener(onItemClick: (Note) -> Unit) {
        this.onItemClickListener = onItemClick
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.apply {
                tvTitle.text = note.title
                if (!note.isSynced) {
                    ivSynced.setImageResource(R.drawable.ic_cross)
                    tvSynced.text = "Not Synced"
                } else {
                    ivSynced.setImageResource(R.drawable.ic_check)
                    tvSynced.text = "Synced"
                }
                val dateFormat = SimpleDateFormat("dd.MM.yy, HH:mm", Locale.getDefault())
                val dateString = dateFormat.format(note.date)
                tvDate.text = dateString

                val drawable = ResourcesCompat.getDrawable(itemView.resources, R.drawable.circle_shape, null)
                drawable?.let {
                    val wrappedDrawable = DrawableCompat.wrap(it)
                    val color = Color.parseColor("#${note.color}")
                    DrawableCompat.setTint(wrappedDrawable, color)
                    viewNoteColor.background = wrappedDrawable
                }
            }
        }
    }
}