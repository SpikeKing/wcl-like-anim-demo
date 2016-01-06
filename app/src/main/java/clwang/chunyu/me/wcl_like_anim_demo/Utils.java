package clwang.chunyu.me.wcl_like_anim_demo;

/**
 * 工具类
 * <p/>
 * Created by wangchenlong on 16/1/5.
 */
public class Utils {

    // 映射到下一个域
    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    // 中间值
    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }
}
