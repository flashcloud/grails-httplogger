package grails.plugins.httplogger;


import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @author Artur Gajowy <artur.gajowy@gmail.com>
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] inputBytes;
    private StringServletInputStream inputStream;
    private BufferedReader reader;

    public MultiReadHttpServletRequest(HttpServletRequest httpServletRequest) {
        super(httpServletRequest);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (reader != null) {
            throw new IllegalStateException("getReader() has already been called on this request.");
        }
        if (inputStream == null) {
            inputStream = new StringServletInputStream(getInputBytes());
        }
        return inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (inputStream != null) {
            throw new IllegalStateException("getInputStream() has already been called on this request.");
        }
        if (reader == null) {
            reader = new BufferedReader(new StringReader(getCopiedInput()));
        }
        return reader;
    }

    public String getCopiedInput() throws IOException {
        return new String(getInputBytes(), getCharacterEncoding());
    }

    private byte[] getInputBytes() throws IOException {
        initializePostParametersBeforeItsTooLate();
        if (inputBytes == null) {
            inputBytes = IOUtils.toByteArray(getRequest().getInputStream());
        }
        return inputBytes;
    }

    private void initializePostParametersBeforeItsTooLate() {
        getParameterMap();
    }

    @Override
    public String getCharacterEncoding() {
        if (getRequest().getCharacterEncoding() == null){
            try {
                getRequest().setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 is unsupported. The world's decay is complete.", e);
            }
        }
        return getRequest().getCharacterEncoding();
    }

    private static class StringServletInputStream extends ServletInputStream {
        int index = 0;
        byte[] inputBytes;

        private StringServletInputStream(byte[] inputBytes) {
            this.inputBytes = inputBytes;
        }

        @Override
        public int read() throws IOException {
            return index == inputBytes.length ? -1 : inputBytes[index++];
        }
    }
}
