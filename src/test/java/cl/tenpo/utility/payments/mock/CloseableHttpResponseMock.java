package cl.tenpo.utility.payments.mock;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.springframework.http.HttpStatus;

@SuppressWarnings("deprecation")
public class CloseableHttpResponseMock implements CloseableHttpResponse
{
	private final String stringEntity;
	private final HttpStatus httpStatus;
	private final ContentType contentType;

	public CloseableHttpResponseMock()
	{
		stringEntity = "{}";
		httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		contentType = ContentType.APPLICATION_JSON;
	}

	public CloseableHttpResponseMock(final String stringEntity, final HttpStatus httpStatus)
	{
		this.stringEntity = stringEntity;
		this.httpStatus = httpStatus;
		contentType = ContentType.APPLICATION_JSON;
	}

	public CloseableHttpResponseMock(final String stringEntity, final HttpStatus httpStatus, final ContentType contentType)
	{
		this.stringEntity = stringEntity;
		this.httpStatus = httpStatus;
		this.contentType = contentType;
	}

	@Override
	public StatusLine getStatusLine() {
		return new BasicStatusLine(HttpVersion.HTTP_1_1, httpStatus.value(), httpStatus.getReasonPhrase());
	}

	@Override
	public void setStatusLine(final StatusLine statusline) {
	}

	@Override
	public void setStatusLine(final ProtocolVersion ver, final int code) {
	}

	@Override
	public void setStatusLine(final ProtocolVersion ver, final int code, final String reason) {
	}

	@Override
	public void setStatusCode(final int code) throws IllegalStateException {
	}

	@Override
	public void setReasonPhrase(final String reason) throws IllegalStateException {
	}

	@Override
	public HttpEntity getEntity() {
		return new StringEntity(stringEntity, contentType);
	}

	@Override
	public void setEntity(final HttpEntity entity) {
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public void setLocale(final Locale loc) {
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return null;
	}

	@Override
	public boolean containsHeader(final String name) {
		return false;
	}

	@Override
	public Header[] getHeaders(final String name) {
		return null;
	}

	@Override
	public Header getFirstHeader(final String name) {
		return null;
	}

	@Override
	public Header getLastHeader(final String name) {
		return null;
	}

	@Override
	public Header[] getAllHeaders() {
		return null;
	}

	@Override
	public void addHeader(final Header header) {
	}

	@Override
	public void addHeader(final String name, final String value) {
	}

	@Override
	public void setHeader(final Header header) {
	}

	@Override
	public void setHeader(final String name, final String value) {

	}

	@Override
	public void setHeaders(final Header[] headers) {
	}

	@Override
	public void removeHeader(final Header header) {
	}

	@Override
	public void removeHeaders(final String name) {
	}

	@Override
	public HeaderIterator headerIterator() {
		return null;
	}

	@Override
	public HeaderIterator headerIterator(final String name) {
		return null;
	}

	@Override
	public HttpParams getParams() {

		return null;
	}

	@Override
	public void setParams(final HttpParams params) {
	}

	@Override
	public void close() throws IOException {
	}
}
