package com.permaidev.financemanager.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.permaidev.financemanager.R;
import com.permaidev.financemanager.adapters.CustomAdapter;
import com.permaidev.financemanager.transactionDb.AppDatabase;
import com.permaidev.financemanager.transactionDb.AppExecutors;
import com.permaidev.financemanager.transactionDb.TransactionEntry;
import com.permaidev.financemanager.transactionDb.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;


public class ExpenseFragment extends Fragment {

    private static final String LOG_TAG = ExpenseFragment.class.getSimpleName();

    private RecyclerView rv;
    private List<TransactionEntry> transactionEntries;
    private CustomAdapter customAdapter;

    public TransactionViewModel transactionViewModel;

    private AppDatabase mAppDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_expense,container,false);
        rv=view.findViewById(R.id.transactionRecyclerView);
        rv.setHasFixedSize(true);
        transactionEntries = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAppDb = AppDatabase.getInstance(getContext());



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                // COMPLETED (1) Get the diskIO Executor from the instance of AppExecutors and
                // call the diskIO execute method with a new Runnable and implement its run method
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();

                        List<TransactionEntry> transactionEntries = customAdapter.getTransactionEntries();
                        mAppDb.transactionDao().removeExpense(transactionEntries.get(position));

                    }
                });

                Snackbar.make(view,"Transaction Deleted",Snackbar.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(rv);

        setupViewModel();


        return view;
    }

    public void setupViewModel(){
        transactionViewModel = ViewModelProviders.of(this)
                .get(TransactionViewModel.class);

        transactionViewModel.getExpenseList()
                .observe(this, new Observer<List<TransactionEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TransactionEntry> transactionEntriesFromDb) {
                        Log.i(LOG_TAG,"Actively retrieving from DB");


                        transactionEntries = transactionEntriesFromDb;
//                        Logging to check DB values
                        for (int i =0 ; i < transactionEntries.size() ; i++){
                            String description = transactionEntries.get(i).getDescription();
                            int amount = transactionEntries.get(i).getAmount();
                            //Log.i("DESCRIPTION AMOUNT",description + String.valueOf(amount));
                        }

//                        Setting Adapter
                        customAdapter=new CustomAdapter(getActivity(),transactionEntries);
                        rv.setAdapter(customAdapter);
                    }
                });
    }
}
