package com.comp90018.comp90018.ui.createTrip;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        String[] modes = {"Blitz", "Medium", "Leisure"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMode.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        navController= Navigation.findNavController(requireView());

        binding.buttonBack.setOnClickListener(v-> backToHome());

        // Set up click listener for the submit button
        binding.buttonNext.setOnClickListener(v -> submitTrip());

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
        // 获取用户输入的内容
        String tripName = binding.editTextTripTitle.getText().toString();
        String city = binding.editTextLocation.getText().toString();
        String mode = binding.spinnerMode.getSelectedItem() != null ? binding.spinnerMode.getSelectedItem().toString() : "";

        // 检查输入内容是否为空
        if (TextUtils.isEmpty(tripName)) {
            Toast.makeText(requireContext(), "Please enter a trip name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(city)) {
            Toast.makeText(requireContext(), "Please enter a city", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mode)) {
            Toast.makeText(requireContext(), "Please select a mode of transportation", Toast.LENGTH_SHORT).show();
            return;
        }

        // 如果所有检查通过，继续创建 TotalPlan 并进行导航
        TotalPlan totalPlan = new TotalPlan();
        totalPlan.setName(tripName);
        totalPlan.setCity(city);
        totalPlan.setMode(mode);

        // 更新 ViewModel 数据
        viewModel.updateLiveData(totalPlan);

        // 执行导航操作
        navController.navigate(R.id.action_create_trip_to_duration);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}