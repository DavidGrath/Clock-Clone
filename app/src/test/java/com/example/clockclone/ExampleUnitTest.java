package com.example.clockclone;

import org.junit.Test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    Scheduler s = Schedulers.newThread();
    Random random = new Random();

    @Test
    public void simpleRxTest() {
        ArrayList<Maybe<String>> maybes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Maybe<String> maybe = nextMaybe(i);
            maybes.add(maybe);
        }
        Maybe.merge(maybes)
                .subscribe(s -> setResult("SUCCESS"), ex -> {
                    setResult("ERROR");
                });
        System.out.println(result);
    }

    String result = "UNKNOWN";
    private void setResult(String r) {
        result = r;
    }

    private Maybe<String> nextMaybe(int i) {
        String s = Integer.toString(i);
        if (i == 11) {
            return Maybe.<String>error(new RuntimeException())
                    .delay(6, TimeUnit.SECONDS);
        }
        Maybe<String> m = Maybe.just(s)
                .delay(3, TimeUnit.SECONDS);
        m.subscribe((st) -> {
            System.out.println("SUCC: " + st);
        });
        return m;
    }

    @Test
    public void sdfTest() {
        DateFormat sdf = DateFormat.getTimeInstance(DateFormat.SHORT);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("America/Adak"));
        String now = sdf.format(calendar.getTime());
        sdf.setTimeZone(TimeZone.getTimeZone("Africa/Addis_Ababa"));
        String other = sdf.format(calendar.getTime());
        System.out.println("Now: " + now);
        System.out.println("Other: " + other);
    }

    @Test
    public void anotherRxTest() {
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int start = 11;
        BehaviorSubject<String> stringBehaviorSubject = BehaviorSubject.create();
        BehaviorSubject<Integer> integerBehaviorSubject = BehaviorSubject.create();
        Observable<String> merged = Observable.combineLatest(stringBehaviorSubject, integerBehaviorSubject, (s, i) ->{
            return s + (i*2);
        });
        merged.subscribe((s)-> {
            System.out.println("Next: " + s);
        });
        for(int i = 0; i < 10; i++) {
            stringBehaviorSubject.onNext(letters[i]);
            if(i % 2 == 0) {
                integerBehaviorSubject.onNext(start);
                start++;
            }
        }
    }
}