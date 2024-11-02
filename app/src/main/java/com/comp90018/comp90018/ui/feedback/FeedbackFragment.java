package com.comp90018.comp90018.ui.feedback;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.TotalPlan;
import com.comp90018.comp90018.service.FirebaseService;
import com.comp90018.comp90018.service.GPTService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private RatingBar ratingBarEnjoyment;
    private RadioGroup radioGroupTiredness;
    private EditText etOtherFeedback;
    private Button btnFinish;
    private NavController navController;
    private LinearLayout attractionContainer; // CheckBoxを動的に追加するコンテナ

    private String tripInfo;
    private String todayDate;
    private ArrayList<String> todaysAttractions;  // その日のアトラクションを保持
    private ArrayList<String> upcomingPlans; // 表示はしないが保持しておく
    private Integer stepCount;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // レイアウトを読み込む
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        // UIコンポーネントを初期化
        ratingBarEnjoyment = view.findViewById(R.id.ratingBarEnjoyment);
        radioGroupTiredness = view.findViewById(R.id.radioGroupTiredness);
        etOtherFeedback = view.findViewById(R.id.etOtherFeedback);
        btnFinish = view.findViewById(R.id.btnFinish);
        attractionContainer = view.findViewById(R.id.attractionContainer); // CheckBoxを追加するコンテナ

        btnFinish.setOnClickListener(v -> collectFeedback());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireView());

        // PlanFragmentから送られてきたデータを保持する
        Bundle bundle = getArguments();
        if (bundle != null) {
            tripInfo = bundle.getString("tripInfo", "Unknown Trip");
            todayDate = bundle.getString("todayDate", "Unknown Date");
            todaysAttractions = bundle.getStringArrayList("todaysAttractions"); // その日のアトラクションを取得
            upcomingPlans = bundle.getStringArrayList("upcomingPlans");

            stepCount = bundle.getInt("stepCount");

            // 渡されたアトラクション（upcomingPlans）をCheckBoxとして動的に追加する
            if (todaysAttractions != null) {
                for (String attraction : todaysAttractions) {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(attraction);
                    attractionContainer.addView(checkBox); // コンテナにCheckBoxを追加
                }
            }
        }
    }

    private void collectFeedback() {
        // Enjoyment（楽しさ）の評価をintに変換
        int enjoyment = (int) ratingBarEnjoyment.getRating();

        // Tiredness Levelの選択されたRadioButtonのIDを取得
        int tirednessLevel = radioGroupTiredness.getCheckedRadioButtonId();

        // Other Feedbackのテキストを取得
        String otherFeedback = etOtherFeedback.getText().toString().trim();

        //


        //没有这个功能
//        // 選択されたアトラクションを収集
//        List<String> selectedAttractions = new ArrayList<>();
//        for (int i = 0; i < attractionContainer.getChildCount(); i++) {
//            View child = attractionContainer.getChildAt(i);
//            if (child instanceof CheckBox) {
//                CheckBox checkBox = (CheckBox) child;
//                if (checkBox.isChecked()) {
//                    selectedAttractions.add(checkBox.getText().toString());
//                }
//            }
//        }

        // TotalPlanのインスタンスを作成し、必要なデータをセット
        TotalPlan totalPlan = new TotalPlan();
        totalPlan.setCity("City Name");  // 都市名をセット
        totalPlan.setStartDate(new Date());  // 開始日を現在日付に設定
        totalPlan.setDuration(5);  // 旅行の期間をセット（例: 5日間）
        totalPlan.setMode("car");  // 旅行のモード（例: 車）

        // Journeyオブジェクトを作成し、targetViewPointMapに追加
        Map<String, Journey> targetViewPointMap = new HashMap<>();
//        for (String attraction : selectedAttractions) {
//            Journey journey = new Journey();  // Journeyオブジェクトを作成
//            journey.setName(attraction);  // アトラクション名をセット
//            journey.setNotes("User visited this place.");  // 任意のメモをセット
//            // 緯度・経度などのデータがある場合は追加
//            journey.setLatitude(0.0);  // 仮の値
//            journey.setLongitude(0.0);  // 仮の値
//            targetViewPointMap.put(attraction, journey);  // Mapに追加
//        }
        totalPlan.setTargetViewPoint(targetViewPointMap);  // TotalPlanにセット

        // GPTリクエストの呼び出し
        GPTService.getInstance().getDayJourneyPlanByFeedback(totalPlan,enjoyment,tirednessLevel, otherFeedback,stepCount, new GPTService.GPTCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("FeedbackFragment",result);
                List<Journey> journeySet = parseResultToJourneys(result,totalPlan.getTargetViewPoint());
                DayPlan dayPlan  = new DayPlan();
                dayPlan.setJourneys(journeySet);
                Date date = null;
                List<DayPlan> dayPlans = totalPlan.getDayPlans();

                if (!dayPlans.isEmpty()) {
                    // 获取最后一个 DayPlan 的日期
                    DayPlan lastDayPlan = dayPlans.get(dayPlans.size() - 1);
                    date = lastDayPlan.getDate();

                    // 使用 Calendar 增加一天
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // 增加一天

                    // 获取增加一天后的日期
                    date = calendar.getTime();
                }

                dayPlan.setDate(date);
                totalPlan.addDayPlans(dayPlan);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 当前用户ID

                FirebaseService.getInstance().uploadTotalPlan(totalPlan,userId,
                        documentReference -> {
                            // 处理 DocumentReference，例如输出新文档的 ID
                            Log.d("PlanService", "DocumentSnapshot written with ID: " + documentReference.getId());
                            Toast.makeText(getContext(), "TotalPlan saved successfully!", Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Log.w("PlanService", "Error adding document", e);
                            Toast.makeText(getContext(), "Failed to save TotalPlan", Toast.LENGTH_SHORT).show();
                        });
                // TODO：跳转到新的plan界面，显示新的东西
                // GPTからの結果をUIに反映
                Toast.makeText(getContext(), "Next day's plan: " + result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Failed to get plan: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public List<Journey> parseResultToJourneys(String result, Map<String, Journey> journeyMap) {
        List<Journey> journeyList = new ArrayList<>();

        // 拆分 result 字符串，去掉空格并按逗号、回车或换行符分隔
        String[] ids = result.split("[,\\s]+");

        // 遍历解析的编号，检查是否为有效数字，获取对应的 Journey 对象并添加到列表中
        for (String idStr : ids) {
            // 使用正则表达式检查是否是数字
            if (idStr.matches("\\d+")) { // 仅匹配数字
                Journey journey = journeyMap.get(idStr); // 直接使用字符串从 map 中获取 Journey
                if (journey != null) {
                    journeyList.add(journey); // 添加到结果列表
                } else {
                    Log.e("PlanService", "No Journey found for ID: " + idStr);
                }
            } else {
                Log.e("PlanService", "Ignoring non-numeric ID: " + idStr);
            }
        }

        return journeyList;
    }

}
