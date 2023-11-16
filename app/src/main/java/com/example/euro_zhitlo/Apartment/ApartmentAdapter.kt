import androidx.appcompat.app.AppCompatActivity

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.RequestListener
import com.example.euro_zhitlo.R
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

class ApartmentAdapter(
    private val context: Context,
    private val apartments: List<Apartment>
) : RecyclerView.Adapter<ApartmentAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(apartment: Apartment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.apartment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apartments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val apartment = apartments[position]

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(apartment)
        }

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 25f
        circularProgressDrawable.start()

        val activity = (context as? AppCompatActivity)
        if (activity != null && !activity.isDestroyed && !activity.isFinishing) {
            Glide.with(activity)
                .load(apartment.image)
                .placeholder(circularProgressDrawable)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .transition(DrawableTransitionOptions.withCrossFade(getCrossFadeFactory()))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        circularProgressDrawable.stop()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        circularProgressDrawable.stop()
                        return false
                    }
                })
                .into(holder.imageView)
        }

        holder.titleTextView.text = apartment.title
        holder.locationTextView.text = apartment.country + ", " + apartment.city
        if(apartment.price == 0){
            holder.priceTextView.text = "Free /"
        }
        else {
            holder.priceTextView.text = apartment.price.toString() + "€ /"
        }
        if (apartment.access) {
            holder.accessTextView.text = "Available"
        } else {
            holder.image_availiable.setImageResource(R.drawable.booked_icon)
            holder.accessTextView.text = "Booked"
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView1)
        val image_availiable: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.textView_title)
        val locationTextView: TextView = itemView.findViewById(R.id.textView_location)
        val priceTextView: TextView = itemView.findViewById(R.id.textView_price)
        val accessTextView: TextView = itemView.findViewById(R.id.textView_access)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    private fun getCrossFadeFactory(): DrawableCrossFadeFactory {
        return DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
    }
}
