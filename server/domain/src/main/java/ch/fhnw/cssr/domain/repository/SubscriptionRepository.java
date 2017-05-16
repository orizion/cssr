package ch.fhnw.cssr.domain.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.fhnw.cssr.domain.Subscription;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

	public List<Subscription> findByPresentationId(int presentationId);
}