package de.fhws.fiw.fwpm.attendance.csvUtility;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.util.CSVUtil;
import com.googlecode.jcsv.writer.CSVColumnJoiner;
import com.googlecode.jcsv.writer.CSVEntryConverter;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;
import com.googlecode.jcsv.writer.internal.DefaultCSVEntryConverter;
import org.apache.commons.codec.Charsets;

import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public class CSVExporter<T> {
	private final Collection<T> query;
	private final Collection<String> headers;
	private final CSVEntryConverter<T> converter;

	public CSVExporter(Collection<T> query, Collection<String> headers, CSVEntryConverter<T> converter) {
		this.query = query;
		this.converter = converter;
		this.headers = headers;
	}


	private void writeHeaders(Writer writer) throws IOException {
		if(this.headers != null) {
			CSVWriter csvWriter = (new CSVWriterBuilder(writer)).entryConverter(new DefaultCSVEntryConverter()).columnJoiner(new CSVExporter.QuotingCSVColumnJoiner()).build();
			csvWriter.write(this.headers.toArray(new String[0]));
		}
	}

	private void writeValues(Writer writer) throws IOException {
		CSVWriter csvWriter = (new CSVWriterBuilder(writer)).entryConverter(this.converter).columnJoiner(new CSVExporter.QuotingCSVColumnJoiner()).build();
		Iterator i$ = this.query.iterator();

		while(i$.hasNext()) {
			Object e = i$.next();
			csvWriter.write(e);
		}

	}

	public StreamingOutput toStreamingOutput()
	{
		return output ->
		{
			OutputStreamWriter writer = new OutputStreamWriter( output, Charsets.UTF_8 );
			CSVExporter.this.write( writer );
			writer.flush();
		};
	}

	public void write(Writer writer)
	{
		try
		{
			this.writeHeaders( writer );
			this.writeValues( writer );
		} catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	public static class QuotingCSVColumnJoiner implements CSVColumnJoiner {
		public QuotingCSVColumnJoiner() {
		}

		public String joinColumns(String[] data, CSVStrategy strategy) {
			String delimiter = String.valueOf(strategy.getDelimiter());
			String quote = String.valueOf(strategy.getQuoteCharacter());
			String doubleQuote = quote + quote;

			for(int i = 0; i < data.length; ++i) {
				if(data[i] == null) {
					data[i] = "";
				}

				data[i] = data[i].replaceAll(Pattern.quote(quote), doubleQuote);
				data[i] = quote + data[i] + quote;
			}

			return CSVUtil.implode(data, delimiter);
		}
	}
}
