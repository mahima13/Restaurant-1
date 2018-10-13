package audioapk.com.example.android.restaurant;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Chef extends AppCompatActivity {

    private DatabaseReference myRef;
    private Random random;
    private ArrayList<String> keys = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);


        final ArrayList<String> item = new ArrayList<>();
        final ListAdaptor listAdaptor = new ListAdaptor(item);
        random = new Random();
        myRef = FirebaseDatabase.getInstance().getReference("orders");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                item.clear();
                keys.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    DataSnapshot cartList = ds.child("cart");
                    keys.add(ds.getKey());
                    StringBuilder value = new StringBuilder();
                    value.append("\n");
                    boolean is = true;
                    for (DataSnapshot oneList : cartList.getChildren()){
                        if (is){
                            value.append("\t").append(oneList.getValue(String.class)).append("\n");
                        }
                        is = !is;
                    }
                    value.append("\t").append(ds.child("total").getValue(String.class)).append("\n");
                    item.add(new String(value));

                }

                listAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.chef_list);
        recyclerView.setAdapter(listAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(item, from, to);
                listAdaptor.notifyItemMoved(from, to);
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                item.remove(viewHolder.getAdapterPosition());
                myRef.child(keys.get(viewHolder.getAdapterPosition())).removeValue();
                listAdaptor.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }



    private class ListAdaptor extends RecyclerView.Adapter<ItemHolder>{


        private ArrayList<String> item;
        ListAdaptor(ArrayList<String> item) {
            this.item = item;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ItemHolder(LayoutInflater.from(Chef.this).inflate(R.layout.card_chef_items,viewGroup,false));

        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
            itemHolder.bind(item.get(i));
        }

        @Override
        public int getItemCount() {
            return item.size();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder{


        TextView singleView;
        ItemHolder(@NonNull View itemView) {
            super(itemView);
            singleView = itemView.findViewById(R.id.card_chef_item);
        }

        void bind(String s) {
            singleView.setText(s);
            singleView.setBackgroundColor(Color.rgb(random.nextInt(150)+100,random.nextInt(150)+100,random.nextInt(150)+100));
        }

    }


//    DatabaseReference myRef;
//    private TextView allOrdersText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chef);
//        allOrdersText = findViewById(R.id.all_orders);
//
//        myRef = FirebaseDatabase.getInstance().getReference("orders");
//        final StringBuilder value = new StringBuilder();
//
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                value.append("\n\n\n");
//                for (DataSnapshot df:dataSnapshot.getChildren()) {
//
//                    value.append(Objects.requireNonNull(df.getValue()).toString()).append("\n\n\n");
//
//                }
//
//                allOrdersText.setText(value);
//                value.setLength(0);
//
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//
//
//    public void deleteList(View view) {
//
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                    appleSnapshot.getRef().removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//    }
}
