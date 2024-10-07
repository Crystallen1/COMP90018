package com.comp90018.comp90018.ui.createTrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.comp90018.comp90018.HomeViewModel;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.databinding.FragmentCreateTripBinding;
import com.comp90018.comp90018.model.TotalPlan;

public class CreateTripFragment extends Fragment {

    private FragmentCreateTripBinding binding;
    private NavController navController;
    private HomeViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using data binding
        binding = FragmentCreateTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        navController= Navigation.findNavController(requireView());

        binding.buttonBack.setOnClickListener(v-> backToHome());

        // Set up click listener for the submit button
        binding.buttonNext.setOnClickListener(v -> submitTrip());

        // Handle date range picker
        binding.editTextStartDate.setOnClickListener(v -> {
            // Handle date range picker logic here
            Toast.makeText(requireContext(), "Date Range Picker Clicked", Toast.LENGTH_SHORT).show();
        });

        // Handle location search box logic here, if needed
        binding.editTextLocation.setOnClickListener(v -> {
            // Handle location search logic here
            Toast.makeText(requireContext(), "Location Search Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void backToHome() {
        navController.navigate(R.id.action_create_trip_to_home);
    }

    private void submitTrip() {
        TotalPlan totalPlan = new TotalPlan();
        totalPlan.setName(binding.editTextTripTitle.getText().toString());
        totalPlan.setCity(binding.editTextLocation.getText().toString());
        totalPlan.setMode(binding.spinnerMode.toString());
        viewModel.updateLiveData(totalPlan);
        navController.navigate(R.id.action_create_trip_to_duration);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}