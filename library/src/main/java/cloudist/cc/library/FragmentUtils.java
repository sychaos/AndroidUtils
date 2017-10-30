package cloudist.cc.library;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class FragmentUtils {

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    @SuppressLint("RestrictedApi")
    public static void addFragmentToBackStack(@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, int frameId) {

        Fragment mLastFragment = null;
        for (@SuppressLint("RestrictedApi") int i = fragmentManager.getFragments().size() - 1; i >= 0; i--) {
            if (fragmentManager.getFragments().get(i) != null) {
                mLastFragment = fragmentManager.getFragments().get(i);
                break;
            } else {
                mLastFragment = null;
            }
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        Fragment的出入场动画
//        transaction.setCustomAnimations(R.anim.push_left_in_no_alpha,
//                R.anim.push_left_out_no_alpha,
//                R.anim.push_right_in_no_alpha,
//                R.anim.push_right_out_no_alpha
//        );
        if (mLastFragment != null) {
            transaction.hide(mLastFragment);
        } else {
            // do nothing
        }
        transaction.add(frameId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
