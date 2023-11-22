import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth

class ApartmentAdapter(
    private val context: Context,
    private val apartments: List<Apartment>
) : RecyclerView.Adapter<ApartmentAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null
    private val mAuth = FirebaseAuth.getInstance()


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

        apartment.updateApartmentImage(mAuth,holder.imageView,context.applicationContext)

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
