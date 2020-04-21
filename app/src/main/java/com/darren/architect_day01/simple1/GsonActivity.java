package com.darren.architect_day01.simple1;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darren.architect_day01.BaseApplication;
import com.darren.architect_day01.MyParameterizedTypeImpl;
import com.darren.architect_day01.R;
import com.darren.architect_day01.adapter.MyIntegerTypeAdapter;
import com.darren.architect_day01.adapter.UserTypeAdapter;
import com.darren.architect_day01.data.entity.Article;
import com.darren.architect_day01.data.entity.ModifierSample;
import com.darren.architect_day01.data.entity.Result;
import com.darren.architect_day01.data.entity.SimpleResult;
import com.darren.architect_day01.data.entity.User;
import com.darren.architect_day01.data.repsonse.BaseRes;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GsonActivity extends AppCompatActivity {

    private static final String TAG = "AppMainActivity";
    String testUrl = "";
    private TextView mTextView;
    private ImageView mIv;
    private String mData3;
    private String mData2;
    private String mData4;
    private String mData1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_gson);
        mTextView = (TextView) findViewById(R.id.tv);
        mIv = (ImageView) findViewById(R.id.iv);
        // data 为 object 的情况
        mData1 = "{\"code\":\"0\",\"message\":\"success\",\"data\":{\"name\":\"armyliu\"}}";
// data 为 array 的情况
        //解析不是按顺序的
        mData2 = "{\"code\":\"0\",\"message\":\"success\",\"data\":[{\"name\":\"怪盗kidou\"}," +
                "{\"name\":\"keepon\"}" +
                "]}";
        mData3 = "{\"code\":\"0\",\"message\":\"success\",\"data\":[{\"name\":\"怪盗kidou\"}" +
                "]}";
        mData4 = "{\"data\":[{\"name\":\"keepon\"}" +
                "]}";
    }

    public void testBitmap(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cspro_icon_robot);
        mIv.setImageBitmap(bitmap);
        // if(bitmap!=null){
        //     bitmap.recycle();
        // }
    }

    public void gsonDemo(View view) {
        testListUser();
    }

    public void gsonDemoCorrect1(View view) {
        correctExample();
    }

    public void gsonDemoCorrect2(View view) {
        correctExample2();
    }

    public void gsonDemoInCorrect(View view) {
        incorrectSample();
    }

    public void gsonDemoInCorrect2(View view) {
        incorrectSample2();
    }

    private void incorrectSample() {
        //断点调试返回的是com.darren.architect_day01.data.entity.Result<java.util.List<T>>
        Result<List<Object>> listResult = fromJsonArrayError(mData3);
        Log.e("TAG", "AppMainActivity incorrectSample Result:" + listResult);
        if (listResult != null && listResult.data != null) {
            for (Object datum : listResult.data) {
                if (datum instanceof User) {
                    User user = (User) datum;
                    Log.e("TAG", "AppMainActivity incorrectSample user:" + user.name);
                }
                if (datum instanceof List) {
                    Log.e("TAG", "AppMainActivity incorrectSample  is List:");
                }
                Log.e("TAG",
                        "AppMainActivity for incorrectSample Result-------------:" + datum + " " +
                                "listResult" +
                                ".data=" + listResult.data);
            }
        }
    }

    private void incorrectSample2() {
        //断点调试返回的是com.darren.architect_day01.data.entity.Result<java.util.List<T>>
        SimpleResult<List<Object>> listResult = fromJsonArrayError2(mData4);
        Log.e("TAG", "AppMainActivity incorrectSample2 SimpleResult:" + listResult);
        if (listResult != null && listResult.data != null) {
            for (Object datum : listResult.data) {
                if (datum instanceof User) {
                    User user = (User) datum;
                    Log.e("TAG",
                            "AppMainActivity incorrectSample2  SimpleResult user:" + user.name);
                }
                if (datum instanceof List) {
                    Log.e("TAG", "AppMainActivity incorrectSample2  SimpleResult is List:");
                }
                Log.e("TAG",
                        "AppMainActivity for incorrectSample2 SimpleResult-------------:" + datum +
                                " listResult" +
                                ".data=" + listResult.data);
            }
        }
    }

    public void gsonDemoObject(View view) {
        Result<User> listResult1 = fromJsonObject(mData1, User.class);
        Log.e("TAG", "AppMainActivity gsonDemoObject Result:" + listResult1);
        User data = listResult1.data;
        Log.e("TAG", "AppMainActivity Result gsonDemoObject data:" + data);

    }

    private void correctExample() {
        Result<List<User>> listResult1 = fromJsonArray(mData3, User.class);
        Log.e("TAG", "AppMainActivity correctExample Result:" + listResult1);
        for (User datum : listResult1.data) {
            if (datum instanceof User) {
                User user = (User) datum;
                Log.e("TAG", "AppMainActivity Result correctExample user:" + user);
            }
        }
    }

    private void correctExample2() {
        SimpleResult<List<User>> listResult1 = fromJsonArray2(mData4, User.class);
        Log.e("TAG", "AppMainActivity correctExample2 SimpleResult:" + listResult1);
        for (User datum : listResult1.data) {
            if (datum instanceof User) {
                User user = (User) datum;
                Log.e("TAG", "AppMainActivity SimpleResult correctExample2 user:" + user);
            }
        }
    }

    //这个肯定是不行的
    public <T> Result<List<T>> fromJsonArrayError(String json) {
        Type type = new TypeToken<Result<List<T>>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    //这个肯定是不行的 T 处理T的TypeAdapter为ObjectTypeAdapter
    public <T> SimpleResult<List<T>> fromJsonArrayError2(String json) {
        Type type = new TypeToken<SimpleResult<List<T>>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public <T> Result<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = new MyParameterizedTypeImpl(Result.class, new Class[]{clazz});
        return new Gson().fromJson(json, type);
    }

    // 处理 data 为 array 的情况
    public <T> Result<List<T>> fromJsonArray(String json, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new MyParameterizedTypeImpl(List.class, new Class[]{clazz});
        Log.e("TAG", "AppMainActivity fromJsonArray listType:" + listType);
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new MyParameterizedTypeImpl(Result.class, new Type[]{listType});
        return new Gson().fromJson(json, type);
    }

    public <T> SimpleResult<List<T>> fromJsonArray2(String json, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new MyParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new MyParameterizedTypeImpl(SimpleResult.class, new Type[]{listType});
        return new Gson().fromJson(json, type);
    }

    private void testUser() {
        Gson gson = new Gson();
        String jsonString = "{\"name\":\"怪盗kidou\"}";
        User user = gson.fromJson(jsonString, User.class);
        Log.e("TAG", "AppMainActivity testUser:" + user.name);
    }

    private void testStringShuzu() {
        Gson gson = new Gson();
        String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
        String[] strings = gson.fromJson(jsonArray, String[].class);
        Log.e("TAG", "AppMainActivity testStringShuzu:" + strings);
        // 不能带泛型
        // List<String> stringList = gson.fromJson(jsonArray, List<String>.class);
        // Log.e("TAG", "AppMainActivity test1:" +strings);
    }

    private void testListString() {
        Gson gson = new Gson();
        String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
        List<String> stringList = gson.fromJson(jsonArray, new TypeToken<List<String>>() {
        }.getType());
        Log.e("TAG", "AppMainActivity testListString:" + stringList);
    }

    private void testListUser() {
        Gson gson = new Gson();
        Result<List<User>> userList = gson.fromJson(mData2, new TypeToken<Result<List<User>>>() {
        }.getType());
        Log.e("TAG", "AppMainActivity testListUser:" + userList);
    }

    private void testAritcle() {
        Gson gson = new Gson();
        String jsonString = "{\"url\":\"http://www.baidu.com\"}";
        Article article = gson.fromJson(jsonString, Article.class);
        Log.e("TAG", "AppMainActivity test3 test3:" + article.url);
    }


    public void jsonReader(View view) throws IOException {
        String json = "{\"name\":\"怪盗aa\",\"age\":\"24\"}";
        User user = new User();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject(); // throws IOException
        while (reader.hasNext()) {
            String s = reader.nextName();
            switch (s) {
                case "name":
                    user.name = reader.nextString();
                    break;
                case "age":
                    user.age = reader.nextInt(); //自动转换
                    break;
                case "email":
                    user.email = reader.nextString();
                    break;
                default:
                    break;
            }
        }
        reader.endObject(); // throws IOException
        Log.e(TAG, "AppMainActivity jsonReader:" + user.name);
    }

    public void jsonStream(View view) throws IOException {
        // Gson gson = new Gson();
        // User user = new User("怪盗kidou",24,"ikidou@example.com");
        // gson.toJson(user,System.out); // 写到控制台

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(System.out));
        writer.beginObject() // throws IOException
                .name("name").value("怪盗kidou")
                .name("age").value(24)
                .name("email").nullValue() //演示null
                .endObject(); // throws IOException
        writer.flush(); // throws IOException
    }

    public void gsonBuilder(View view) {
        // Gson gson = new Gson();
        // //可以看出，email字段是没有在json中出现的
        // User user = new User("怪盗kidou",24);
        // System.out.println(gson.toJson(user)); //{"name":"怪盗kidou","age":24}


        // Gson gson = new GsonBuilder()
        //         .serializeNulls()
        //         .create();
        // User user = new User("怪盗kidou", 24);
        // System.out.println(gson.toJson(user)); //{"name":"怪盗kidou","age":24,"email":null}

        // String  category=  "{ \"id\":1, \"name\": \"电脑\", \"children\": [{ \"id\": 100, \"name\": \"笔记本\" }, {\"id\": 101, \"name\": \"台式机\" }]}";
        //  Gson gson = new GsonBuilder()
        //          .excludeFieldsWithoutExposeAnnotation()
        //          .create();
        //  gson.toJson(category);


        // int version = 5;
        // SinceUntilSample sinceUntilSample = new SinceUntilSample();
        // sinceUntilSample.since = "since";
        // sinceUntilSample.until = "until";
        // Gson gson = new GsonBuilder().setVersion(version).create();
        // System.out.println(gson.toJson(sinceUntilSample));
        //当version <4时，结果：{"until":"until"}
        //当version >=4 && version <5时，结果：{"since":"since","until":"until"}
        //当version >=5时，结果：{"since":"since"}


        //下面的程序排除了privateField 、 finalField 和staticField 三个字段。
        ModifierSample modifierSample = new ModifierSample();
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                .create();
        System.out.println(gson.toJson(modifierSample));
// 结果：{"publicField":"public","protectedField":"protected","defaultField":"default"}


        Gson gson2 = new GsonBuilder()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        // 这里作判断，决定要不要排除该字段,return true为排除
                        if ("finalField".equals(f.getName())) {
                            return true; //按字段名排除
                        }
                        Expose expose = f.getAnnotation(Expose.class);
                        if (expose != null && expose.deserialize() == false) {
                            return true; //按注解排除
                        }
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        // 直接排除某个类 ，return true为排除
                        return (clazz == int.class || clazz == Integer.class);
                    }
                })
                .create();


        // FieldNamingPolicy	结果（仅输出emailAddress字段）
        // IDENTITY	{"emailAddress":"ikidou@example.com"}
        // LOWER_CASE_WITH_DASHES	{"email-address":"ikidou@example.com"}
        // LOWER_CASE_WITH_UNDERSCORES	{"email_address":"ikidou@example.com"}
        // UPPER_CAMEL_CASE	{"EmailAddress":"ikidou@example.com"}
        // UPPER_CAMEL_CASE_WITH_SPACES	{"Email Address":"ikidou@example.com"}


        // @SerializedName注解拥有最高优先级，在加有@SerializedName注解的字段上FieldNamingStrategy不生效！
        Gson gson3 = new GsonBuilder()
                .setFieldNamingStrategy(new FieldNamingStrategy() {
                    @Override
                    public String translateName(Field f) {
                        //实现自己的规则
                        return null;
                    }
                })
                .create();
    }

    public void typeAdapter(View view) {
        // jibenjiexi();
        // jibenshengcheng();
        // userTypeAdapter();
        // integerAdapter();
        shujubuyizhi();

        jsonDeserializer();
        jsonserializer();


        typeAdapter4();
    }
    // registerTypeAdapter	registerTypeHierarchyAdapter
    // 支持泛型	是	        否
    // 支持继承	否	        是
    private void shujubuyizhi() {
        // 方案一：
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(List.class, new JsonDeserializer<List<?>>() {
            @Override
            public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonArray()){
                    //这里要自己负责解析了
                    Gson newGson = new Gson();
                    return newGson.fromJson(json,typeOfT);
                }else {
                    //和接口类型不符，返回空List
                    return Collections.EMPTY_LIST;
                }
            }
        }).create();

        // 方案二：
        Gson gson2 = new GsonBuilder().registerTypeHierarchyAdapter(List.class,
                new JsonDeserializer<List<?>>() {
            @Override
            public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonArray()) {
                    JsonArray array = json.getAsJsonArray();
                    Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
                    List list = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        JsonElement element = array.get(i);
                        Object item = context.deserialize(element, itemType);
                        list.add(item);
                    }
                    return list;
                } else {
                    //和接口类型不符，返回空List
                    return Collections.EMPTY_LIST;
                }
            }
        }).create();

        // 1.必须使用registerTypeHierarchyAdapter方法，不然对List的子类无效，但如果POJO中都是使用List
        // ，那么可以使用registerTypeAdapter。
        // 2.对于是数组的情况，需要创建一个新的Gson，不可以直接使用context,
        // 不然gson又会调我们自定义的JsonDeserializer造成递归调用，方案二没有重新创建Gson，那么就需要提取出List<E>中E的类型，然后分别反序列化适合为E手动注册了TypeAdaper的情况。
        //3.从效率上推荐方案二，免去重新实例化Gson和注册其它TypeAdapter的过程。


    }

    // 如果一个被序列化的对象本身就带有泛型，且注册了相应的TypeAdapter，那么必须调用Gson.toJson(Object,Type)，明确告诉Gson对象的类型。
    private void typeAdapter4() {
        Type type = new TypeToken<List<User>>() {
        }.getType();
        TypeAdapter typeAdapter = new TypeAdapter<List<User>>() {
            @Override
            public void write(JsonWriter out, List<User> value) throws IOException {

            }

            @Override
            public List<User> read(JsonReader in) throws IOException {
                return null;
            }
            //略
        };
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(type, typeAdapter)
                .create();
        List<User> list = new ArrayList<>();
        list.add(new User("a", 11));
        list.add(new User("b", 22));
        //注意，多了个type参数
        String result = gson.toJson(list, type);
    }

    private void jsonserializer() {
        // 下面是所有数字都转成序列化为字符串的例子
        JsonSerializer<Number> numberJsonSerializer = new JsonSerializer<Number>() {
            @Override
            public JsonElement serialize(Number src, Type typeOfSrc,
                                         JsonSerializationContext context) {
                return new JsonPrimitive(String.valueOf(src));
            }
        };
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, numberJsonSerializer)
                .registerTypeAdapter(Long.class, numberJsonSerializer)
                .registerTypeAdapter(Float.class, numberJsonSerializer)
                .registerTypeAdapter(Double.class, numberJsonSerializer)
                .create();
        System.out.println(gson.toJson(100.0f));//结果："100.0"

        // 注：registerTypeAdapter必须使用包装类型，所以int.class,long.class,float.class和double.class是行不通的。
        // 同时不能使用父类来替上面的子类型，这也是为什么要分别注册而不直接使用Number.class的原因。


    }

    // 反序列化
    private void jsonDeserializer() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {
                    @Override
                    public Integer deserialize(JsonElement json, Type typeOfT,
                                               JsonDeserializationContext context)
                            throws JsonParseException {
                        try {
                            return json.getAsInt();
                        } catch (NumberFormatException e) {
                            return -1;
                        }
                    }
                })
                .create();
        System.out.println(gson.toJson(100)); //结果：100
        System.out.println(gson.fromJson("\"\"", Integer.class)); //结果-1
    }

    private void integerAdapter() {
        //自定义的会加到前面去
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new MyIntegerTypeAdapter())
                .create();
        System.out.println(gson.toJson(100)); // 结果："100"
        // System.out.println(gson.fromJson("\"\"",Integer.class)); // 结果：-1
        System.out.println(gson.fromJson("11", Integer.class)); // 结果：-1
    }

    private void userTypeAdapter() {
        User user = new User("怪盗kidou", 24);
        user.email = "ikidou@example.com";
        Gson gson = new GsonBuilder()
                //为User注册TypeAdapter
                .registerTypeAdapter(User.class, new UserTypeAdapter())
                .create();
        System.out.println(gson.toJson(user));
        Log.e(TAG, "typeAdapter:" + gson.toJson(user));
    }

    private void jibenshengcheng() {
        Gson gson = new Gson();
        String jsonNumber = gson.toJson(100);       // 100
        String jsonBoolean = gson.toJson(false);    // false
        String jsonString = gson.toJson("String"); //"String"
    }

    private void jibenjiexi() {
        Gson gson = new Gson();
        int i = gson.fromJson("100", int.class);              //100
        double d = gson.fromJson("\"99.99\"", double.class);  //99.99
        boolean b = gson.fromJson("true", boolean.class);     // true
        String str = gson.fromJson("String", String.class);   // String
        Log.e(TAG, "AppMainActivity typeAdapter d:" + d + "  b=" + b);
    }

    public void typeAdapterFactory(View view) {
        // TypeAdapterFactory,见名知意，用于创建TypeAdapter的工厂类，通过对比Type，
        // 确定有没有对应的TypeAdapter，没有就返回null，与GsonBuilder.registerTypeAdapterFactory配合使用
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new TypeAdapterFactory() {
                    @Override
                    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                        return null;
                    }
                })
                .create();
    }

}


