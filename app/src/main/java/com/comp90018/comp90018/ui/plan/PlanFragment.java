package com.comp90018.comp90018.ui.plan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.comp90018.comp90018.adapter.PlanAdapter;
import com.comp90018.comp90018.model.Plan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class PlanFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlanAdapter planAdapter;
    private List<Plan> planList;
    private Calendar calendar = Calendar.getInstance();
    private NavController navController;
    private ImageButton backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load the layout file
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        navController = Navigation.findNavController(requireActivity(), R.id.fragment_container);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the plan list
        planList = new ArrayList<>();
        planList.add(new Plan("2024-10-07", "Peter Coffee", "Breakfast"));
        planList.add(new Plan("2024-10-08", "Great Ocean Road", "Day Trip"));
        planList.add(new Plan("2024-10-09", "Phillip Island", "Penguin Parade"));
        planList.add(new Plan("2024-10-10", "Yarra Valley", "Wine Tasting"));

        // Set adapter
        planAdapter = new PlanAdapter(getContext(), planList, navController);
        recyclerView.setAdapter(planAdapter);

        // Floating button for adding new plans
        FloatingActionButton btnAddPlan = view.findViewById(R.id.btnAddPlan);
        btnAddPlan.setOnClickListener(v -> showAddPlanDialog());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireView());

        backButton = view.findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_plan_to_home));
    }


    private void showAddPlanDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_plan, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView tvPlanTime = dialogView.findViewById(R.id.tvPlanTime);
        EditText etLocationTitle = dialogView.findViewById(R.id.etPlanLocationTitle);
        EditText etDescription = dialogView.findViewById(R.id.etPlanDescription);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        tvPlanTime.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                tvPlanTime.setText(dateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String time = tvPlanTime.getText().toString().trim();
            String locationTitle = etLocationTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (!time.isEmpty() && !locationTitle.isEmpty() && !description.isEmpty()) {
                planList.add(new Plan(time, locationTitle, description));
                planAdapter.notifyItemInserted(planList.size() - 1);
                recyclerView.scrollToPosition(planList.size() - 1);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }
}