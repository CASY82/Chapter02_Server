package kr.hhplus.be.server.domain.point;

public interface PointRepository {
	void save(Point point);
	Point findByUserRefId(Long userRefId);
}
