package cl.tenpo.utility.payments.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>
{
	@EntityGraph("joined")
	public List<Favorite> findByUser(final UUID user);
	public Optional<Favorite> findByUserAndId(final UUID user, final Long id);
	public Optional<Favorite> findByUserAndUtilityIdAndIdentifier(final UUID user, final Long utilityId, final String identifier);
}
