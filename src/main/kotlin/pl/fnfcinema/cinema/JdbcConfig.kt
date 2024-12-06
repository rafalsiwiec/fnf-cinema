package pl.fnfcinema.cinema

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.shows.ShowId
import java.util.*

@Configuration
class JdbcConfig : AbstractJdbcConfiguration() {
    override fun userConverters(): MutableList<Converter<*, *>> = mutableListOf(
        EntityIdWriter(),
        EntityIdReader(::MovieId),
        EntityIdReader(::ShowId),
        EntityIdWriter(),
        CurrencyWriter(),
        CurrencyReader(),
        StaffUserIdWriter(),
        StaffUserIdReader()
    )
}

@WritingConverter
class EntityIdWriter : Converter<EntityId, UUID> {
    override fun convert(source: EntityId): UUID = source.value
}

@ReadingConverter
class EntityIdReader<IdType : EntityId>(private val idConstructor: (UUID) -> IdType) : Converter<UUID, IdType> {
    override fun convert(source: UUID): IdType = idConstructor(source)
}

@WritingConverter
class CurrencyWriter : Converter<Currency, String> {
    override fun convert(source: Currency): String = source.code
}

@ReadingConverter
class CurrencyReader : Converter<String, Currency> {
    override fun convert(source: String): Currency = Currency(source)
}

@WritingConverter
class StaffUserIdWriter : Converter<StaffUserId, UUID> {
    override fun convert(source: StaffUserId): UUID = source.id
}

@ReadingConverter
class StaffUserIdReader : Converter<UUID, StaffUserId> {
    override fun convert(source: UUID): StaffUserId = StaffUserId(source)
}