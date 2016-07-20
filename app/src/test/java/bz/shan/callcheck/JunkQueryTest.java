package bz.shan.callcheck;

import android.content.Context;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;

/**
 * Created by shan on 6/29/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(android.util.Log.class)
public class JunkQueryTest {


    @Mock
    Context mMockContext;

    @Test
    public void testJunkQuery() throws  Exception {
        PowerMockito.mockStatic(Log.class);

        Answer<Void> ans= new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                System.out.println(args[1]);
                return null;
            }
        };
        PowerMockito.when(Log.i(anyString(),anyString())).then(ans);
        PowerMockito.when(Log.d(anyString(),anyString())).then(ans);

        JunkcallQuery2 jq = new JunkcallQuery2();
        JunkcallQuery2.Result entity = jq.check("25251163", mMockContext);
        Thread.sleep(3000);
        System.out.println(entity);

        entity = jq.check("21313145", mMockContext);
        Thread.sleep(3000);
        System.out.println(entity);

        entity = jq.check("28042600", mMockContext);
        Thread.sleep(3000);
        System.out.println(entity);

        entity = jq.check("39706600", mMockContext);
        Thread.sleep(3000);
        System.out.println(entity);



    }
}
