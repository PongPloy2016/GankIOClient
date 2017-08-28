/**
 * Copyright 2017 yidong
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package onlyloveyd.com.gankioclient.utils;

import static onlyloveyd.com.gankioclient.utils.Constant.ONE_DAY;
import static onlyloveyd.com.gankioclient.utils.Constant.ONE_HOUR;
import static onlyloveyd.com.gankioclient.utils.Constant.ONE_MINUTE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import onlyloveyd.com.gankioclient.BuildConfig;
import onlyloveyd.com.gankioclient.R;
import onlyloveyd.com.gankioclient.activity.WebActivity;
import onlyloveyd.com.gankioclient.gsonbean.VersionBean;
import onlyloveyd.com.gankioclient.http.HttpMethods;
import onlyloveyd.com.gankioclient.http.UpdateManager;

/**
 * 文 件 名: PublicTools
 * 创 建 人: 易冬
 * 创建日期: 2017/4/21 09:24
 * 邮   箱: onlyloveyd@gmail.com
 * 博   客: https://onlyloveyd.cn
 * 描   述：工具方法类
 */
public class PublicTools {
    /**
     * 获取目标时间和当前时间之间的差距
     *
     * @param date date
     * @return String
     */
    public static String getTimestampString(Date date) {
        Date curDate = new Date();
        long splitTime = curDate.getTime() - date.getTime();
        if (splitTime < (30 * ONE_DAY)) {
            if (splitTime < ONE_MINUTE) {
                return "刚刚";
            }
            if (splitTime < ONE_HOUR) {
                return String.format("%d分钟前", splitTime / ONE_MINUTE);
            }

            if (splitTime < ONE_DAY) {
                return String.format("%d小时前", splitTime / ONE_HOUR);
            }

            return String.format("%d天前", splitTime / ONE_DAY);
        }
        String result;
        result = "M月d日 HH:mm";
        return (new SimpleDateFormat(result, Locale.CHINA)).format(date);
    }

    /**
     * Date（long） 转换 String
     *
     * @param time   time
     * @param format format
     * @return String
     */
    public static String date2String(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    /**
     * start WebActivity
     */
    public static void startWebActivity(Context context, String url) {
        context.startActivity(getWebIntent(context, url));
    }

    /**
     * get intent by url
     */
    public static Intent getWebIntent(Context context, String url) {
        Intent intent = new Intent();
        intent.setClass(context, WebActivity.class);
        intent.putExtra("URL", url);
        return intent;
    }

    /**
     * hide keyboard
     */
    public static void hide_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * show keyboard
     */
    public static void show_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 保存Bitmap为图片
     */
    public static void saveBitmap(Bitmap bitmap, String picPath) throws Exception {
        File f = new File(picPath + Constant.SUFFIX_JPEG);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bitmap.recycle();
            throw new FileNotFoundException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bitmap.recycle();
            throw new IOException();
        }

    }


    /**
     * 检查更新
     */
    public static void checkUpdate(final Context context, final boolean auto) {
        final ProgressDialog loadingDialog = new ProgressDialog(context);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setTitle("提示");
        loadingDialog.setMessage("正在检测新版本...");
        loadingDialog.setCancelable(false);

        Observer<VersionBean> subscriber = new Observer<VersionBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                if (BuildConfig.YLog) {
                    Log.i("yidong", "onStart " + "\n");
                }
                if (!auto) {
                    loadingDialog.show();
                }
            }

            @Override
            public void onNext(final VersionBean value) {
                loadingDialog.hide();
                if (BuildConfig.YLog) {
                    Log.i("yidong", "check from fir.im success! " + "\n" + value);
                }
                Gson gson = new Gson();
                if (BuildConfig.VERSION_NAME.equals(value.getVersionShort())) {
                    if (!auto) {
                        Toast.makeText(context, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new AlertDialog.Builder(context).setTitle(
                            context.getString(R.string.version_update,
                                    value.getVersionShort()))
                            .setMessage("更新日志：\n" + value.getChangelog())
                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Intent.ACTION_VIEW);
//                                    intent.setData(Uri.parse(Constant.APP_FIR_IM_URL));
//                                    context.startActivity(intent);
                                    UpdateManager updateManager = new UpdateManager(context);
                                    updateManager.setDownUrl(Constant.GITHUB_LATEST_APK);
                                    updateManager.setApkName(
                                            value.getName() + value.getVersionShort()
                                                    + ".apk");
                                    updateManager.showDownloadDialog();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (BuildConfig.YLog) {
                    e.printStackTrace();
                }
                loadingDialog.hide();
                if (!auto) {
                    Toast.makeText(context, "检查更新出现错误，请确保网络连接正常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onComplete() {

            }
        };
        HttpMethods.getInstance().getVersionInfoFromFIR(subscriber, HttpMethods.VERSION_CHECK_URL);

//        FIR.checkForUpdateInFIR(Constant.FIR_API_TOKEN, new VersionCheckCallback() {
//            @Override
//            public void onSuccess(final String versionJson) {
//                loadingDialog.hide();
//                if (BuildConfig.YLog) {
//                    Log.i("yidong", "check from fir.im success! " + "\n" + versionJson);
//                }
//                Gson gson = new Gson();
//                final VersionBean versionBean = gson.fromJson(versionJson, VersionBean.class);
//                if (BuildConfig.VERSION_NAME.equals(versionBean.getVersionShort())) {
//                    if (!auto) {
//                        Toast.makeText(context, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    new AlertDialog.Builder(context).setTitle(
//                            context.getString(R.string.version_update,
//                                    versionBean.getVersionShort()))
//                            .setMessage("更新日志：\n" + versionBean.getChangelog())
//                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
////                                    Intent intent = new Intent();
////                                    intent.setAction(Intent.ACTION_VIEW);
////                                    intent.setData(Uri.parse(Constant.APP_FIR_IM_URL));
////                                    context.startActivity(intent);
//                                    UpdateManager updateManager = new UpdateManager(context);
//                                    updateManager.setDownUrl(Constant.GITHUB_LATEST_APK);
//                                    updateManager.setApkName(
//                                            versionBean.getName() + versionBean.getVersionShort()
//                                                    + ".apk");
//                                    updateManager.showDownloadDialog();
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .show();
//                }
//
//            }
//
//            @Override
//            public void onFail(Exception exception) {
//                if (BuildConfig.YLog) {
//                    exception.printStackTrace();
//                }
//                loadingDialog.hide();
//                if (!auto) {
//                    Toast.makeText(context, "检查更新出现错误，请确保网络连接正常", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onStart() {
//                if (BuildConfig.YLog) {
//                    Log.i("yidong", "onStart " + "\n");
//                }
//                if (!auto) {
//                    loadingDialog.show();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                if (BuildConfig.YLog) {
//                    Log.i("yidong", "onFinish");
//                }
//            }
//        });
    }
}
