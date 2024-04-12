import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Adapters.SizeAdapter
import com.example.giuaky1.Firebase.FirebaseFunction
import com.example.giuaky1.Models.ProductModel
import com.example.giuaky1.Models.SizeModel
import com.example.giuaky1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener

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
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = sizeAdapter
        val product = arguments?.getSerializable("product") as ProductModel
        val productId = product.name
        productId.let {
            FirebaseDatabase.getInstance().getReference("Products").child(it).child("sizes")
        }.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sizeList1.clear()
                for (snapshot in dataSnapshot.children) {
                    val size = snapshot.child("size").getValue(String::class.java)
                    val price = snapshot.child("price").getValue(Double::class.java)
                    if (size != null && price != null) {
                        sizeList1.add(SizeModel(size, price))
                    }
                }
                sizeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("sizesfb", "Fetch data cancelled: ${databaseError.message}")
            }
        })

        val btn_add = view.findViewById<Button>(R.id.btn_add_product_to_cart)
        btn_add.setOnClickListener {
            val selectedSize = sizeAdapter.getSelectedSize() // get the selected SizeModel
            if (selectedSize != null) {
                FirebaseFunction.addToCart(product, selectedSize)
            }
            val dialog: PopupDialog = PopupDialog.getInstance(requireContext())
            dialog.setStyle(Styles.SUCCESS)
                .setHeading("Thành công")
                .setDescription("Thêm sản phẩm vào giỏ hàng thành công!")
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onDismissClicked(dialog: Dialog?) {
                        super.onDismissClicked(dialog)
                    }
                })
            Handler().postDelayed({ dialog.dismissDialog() }, 1500)
        }
    }

    companion object {
        fun newInstance(product: ProductModel) = SizeDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable("product", product)
            }
        }
    }
}