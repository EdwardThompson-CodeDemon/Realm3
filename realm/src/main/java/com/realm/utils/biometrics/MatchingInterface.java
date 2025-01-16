package com.realm.utils.biometrics;

public interface MatchingInterface {

                void on_match_complete(boolean match_found, String mils);
        void on_match_found(String employee_id, String data_index, String match_time, int v_type, int verrification_mode);
        void on_finger_match_found(String fp_id, int score, String match_time);
        void on_match_progress_changed(int progress);
        void on_match_faild_reason_found(int reason, String employee_id);



}
