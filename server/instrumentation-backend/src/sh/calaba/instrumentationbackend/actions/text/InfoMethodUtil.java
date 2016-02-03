package sh.calaba.instrumentationbackend.actions.text;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import sh.calaba.instrumentationbackend.InstrumentationBackend;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InfoMethodUtil {
    public static View getServedView() throws UnexpectedInputMethodManagerStructureException {
        Context context = InstrumentationBackend.instrumentation.getTargetContext();

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            Field servedViewField = InputMethodManager.class.getDeclaredField("mServedView");
            servedViewField.setAccessible(true);

            return (View)servedViewField.get(inputMethodManager);
        } catch (IllegalAccessException e) {
            throw new UnexpectedInputMethodManagerStructureException(e);
        } catch (NoSuchFieldException e) {
            throw new UnexpectedInputMethodManagerStructureException(e);
        }
    }

    public static InputConnection getInputConnection() throws UnexpectedInputMethodManagerStructureException {
        Context context = InstrumentationBackend.instrumentation.getTargetContext();

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            Field servedInputConnectionField = InputMethodManager.class.getDeclaredField("mServedInputConnection");
            servedInputConnectionField.setAccessible(true);

            return (InputConnection) servedInputConnectionField.get(inputMethodManager);
        } catch (IllegalAccessException e) {
            throw new UnexpectedInputMethodManagerStructureException(e);
        } catch (NoSuchFieldException e) {
            throw new UnexpectedInputMethodManagerStructureException(e);
        }
    }

    /*
     Find length of non-formatted text
    */
    public static int getEditableTextLength(Editable editable) {
        return TextUtils.substring(editable, 0, editable.length()).length();
    }

    public static Editable getEditable(View view) {
        Editable editable = null;

        if (view instanceof TextView) {
            editable = ((TextView) view).getEditableText();
        } else {
            try {
                Method m = view.getClass().getMethod("getEditableText");
                m.setAccessible(true);
                Object o = m.invoke(view);

                if (o instanceof Editable) {
                    editable = (Editable) o;
                }
            } catch (NoSuchMethodException e) {
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e) {
            }
        }

        if (editable == null) {
            throw new IllegalStateException("View '" + view + "' is not editable");
        } else {
            return editable;
        }
    }

    public static class UnexpectedInputMethodManagerStructureException extends Exception {
        public UnexpectedInputMethodManagerStructureException() {
            super();
        }

        public UnexpectedInputMethodManagerStructureException(String detailMessage) {
            super(detailMessage);
        }

        public UnexpectedInputMethodManagerStructureException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public UnexpectedInputMethodManagerStructureException(Throwable throwable) {
            super(throwable);
        }
    }
}
