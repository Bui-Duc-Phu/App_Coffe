import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.SizeAdapter
import com.example.giuaky1.Models.SizeModel
import com.example.giuaky1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SizeDialogFragment : BottomSheetDialogFragment() {

    lateinit var sizeAdapter: SizeAdapter
    var sizeList1 = ArrayList<SizeModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_size_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sizeAdapter = SizeAdapter(sizeList1)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = sizeAdapter
        val productId = arguments?.getString("productId")
        productId?.let {
            FirebaseDatabase.getInstance().getReference("Products").child(it).child("sizes")
        }?.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sizeList1.clear() // Clear the list before adding new data
                for (snapshot in dataSnapshot.children) {
                    val size = snapshot.child("size").getValue(String::class.java)
                    val price = snapshot.child("price").getValue(Double::class.java)
                    if (size != null && price != null) {
                        sizeList1.add(SizeModel(size, price))
                    }
                }
                Log.d("sizesfb", "Data from Firebase: $dataSnapshot")
                Log.d("sizesfb", "Size list size: ${sizeList1.size}")
                sizeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("sizesfb", "Fetch data cancelled: ${databaseError.message}")
            }
        })
    }

    companion object {
        fun newInstance(productId: String) = SizeDialogFragment().apply {
            arguments = Bundle().apply {
                putString("productId", productId)
            }
        }
    }
}