package pl.fnfcinema.cinema

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class JdbcConfig : AbstractJdbcConfiguration() {
    override fun userConverters(): MutableList<Converter<*, *>> = mutableListOf(
        CurrencyWriter(),
        CurrencyReader()
    )
}

@WritingConverter
class CurrencyWriter : Converter<Currency, String> {
    override fun convert(source: Currency): String = source.code
}

@ReadingConverter
class CurrencyReader : Converter<String, Currency> {
    override fun convert(source: String): Currency = Currency(source)
}