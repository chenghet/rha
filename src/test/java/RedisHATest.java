import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianwoba.rha.service.CacheService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/app-context-cache-sentinal.xml")
public class RedisHATest {

	@Autowired
	private CacheService cacheService;

	private AtomicInteger readCount = new AtomicInteger(0);
	private AtomicInteger readCount2 = new AtomicInteger(0);
	private AtomicInteger readCount5 = new AtomicInteger(0);
	private AtomicInteger readCount10 = new AtomicInteger(0);
	private AtomicInteger readCount20 = new AtomicInteger(0);
	private AtomicInteger readCount50 = new AtomicInteger(0);

	private AtomicInteger writeCount = new AtomicInteger(0);
	private AtomicInteger writeCount2 = new AtomicInteger(0);
	private AtomicInteger writeCount5 = new AtomicInteger(0);
	private AtomicInteger writeCount10 = new AtomicInteger(0);
	private AtomicInteger writeCount20 = new AtomicInteger(0);
	private AtomicInteger writeCount50 = new AtomicInteger(0);

	// ִ�д���
	public static final int ROUND_COUNT = 5000000;
	public static final int THREAD_NUM = 50;

	@Test
	@Ignore
	public void testObjectInfinitely() throws Exception {
		final Random random = new Random(System.currentTimeMillis());
		System.out.println("===============================");

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("=============hook out==============");
				System.out.println("read >= 2ms < 5ms" + "--" + readCount2.get());
				System.out.println("read >= 5ms < 10ms" + "--" + readCount5.get());
				System.out.println("read >= 10ms < 20ms" + "--" + readCount10.get());
				System.out.println("read >= 20ms < 50ms" + "--" + readCount20.get());
				System.out.println("read > 50ms" + "--" + readCount50.get());

				System.out.println("write >= 2ms < 5ms" + "--" + writeCount2.get());
				System.out.println("write >= 5ms < 10ms" + "--" + writeCount5.get());
				System.out.println("write >= 10ms < 20ms" + "--" + writeCount10.get());
				System.out.println("write >= 20ms < 50ms" + "--" + writeCount20.get());
				System.out.println("write > 50ms" + "--" + writeCount50.get());
			}
		}));

		for (int j = 0; j < THREAD_NUM; j++) {
			// write
			Thread w = new Thread(new Runnable() {
				public void run() {
					String tn = Thread.currentThread().getName();
					// System.out.println("writer-" + tn);
					for (int i = 0; i < ROUND_COUNT / THREAD_NUM; i++) {
						// ���컺�����
						CacheItem cacheItem = new CacheItem();
						cacheItem.setKey("het-test-" + tn + "-" + i);
						cacheItem.setStatus(1);
						cacheItem.setDate(new Date());
						cacheItem.setAnything(random.nextLong());
						byte[] bytes = new byte[126];
						random.nextBytes(bytes);
						cacheItem.setDesc(new String(bytes));
						cacheItem.setDate2(new Date());

						// �������
						long start = System.currentTimeMillis();
						cacheService.putCache("het-test-" + tn + "-" + i, cacheItem);
						long cost = System.currentTimeMillis() - start;

						// ͳ�Ƽ���
						writeCount.getAndIncrement();
						if (cost <= 2) {
							writeCount2.getAndIncrement();
						} else if (cost > 2 && cost <= 5) {
							writeCount5.getAndIncrement();
						} else if (cost > 5 && cost <= 10) {
							writeCount10.getAndIncrement();
						} else if (cost > 10 && cost <= 20) {
							writeCount20.getAndIncrement();
						} else if (cost > 20 && cost <= 50) {
							writeCount50.getAndIncrement();
						}

						// �������
						try {
							Thread.sleep(random.nextInt(3));
						} catch (InterruptedException e) {
							System.err.println(e.getMessage());
						}
					}
				}
			}, "" + j);
			w.setDaemon(true);
			w.start();

			// read
			Thread r = new Thread(new Runnable() {
				public void run() {
					String tn = Thread.currentThread().getName();
					// System.out.println("reader-" + tn);
					for (int i = 0; i < ROUND_COUNT / THREAD_NUM; i++) {

						// �����ȡ
						long start = System.currentTimeMillis();
						cacheService.getCache("het-test-" + tn + "-" + i);
						long cost = System.currentTimeMillis() - start;

						// ͳ��
						readCount.getAndIncrement();
						if (cost <= 2) {
							readCount2.getAndIncrement();
						} else if (cost > 2 && cost <= 5) {
							readCount5.getAndIncrement();
						} else if (cost > 5 && cost <= 10) {
							readCount10.getAndIncrement();
						} else if (cost > 10 && cost <= 20) {
							readCount20.getAndIncrement();
						} else if (cost > 20 && cost <= 50) {
							readCount5.getAndIncrement();
						}

						// �������
						try {
							Thread.sleep(random.nextInt(3));
						} catch (InterruptedException e) {
							System.err.println(e.getMessage());
						}
					}
				}
			}, "" + j);
			r.setDaemon(true);
			r.start();
		}

		// monitor
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (readCount.get() >= ROUND_COUNT && writeCount.get() >= ROUND_COUNT) {
						System.out.println("==============================");
						System.out.println("ִ�����ˣ�");
						System.exit(0);
					}

					if (readCount50.get() >= ROUND_COUNT * 0.2 || writeCount50.get() >= ROUND_COUNT * 0.2) {
						System.out.println("==============================");
						System.out.println("������Ӧ����50ms����20%��Ҫ���ˣ�");
						System.exit(0);
					}

					try {
						Thread.sleep(random.nextInt(5));
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		}).run();

		System.in.read();
	}

	@Test
	public void testExpire() {
		Set<String> keys = cacheService.getRedisTemplate().keys("het-test-*");
		for (String key : keys) {
			cacheService.expire(key, 10, TimeUnit.SECONDS);
		}
	}

	@Test
	@Ignore
	public void testKeys() throws Exception {
		final Random random = new Random(System.currentTimeMillis());
		System.out.println("===============================");

		for (int j = 0; j < 10; j++) {
			new Thread(new Runnable() {
				public void run() {
					String tn = Thread.currentThread().getName();
					System.out.println("reader-" + tn);

					while (true) {
						Set<String> keys = cacheService.getRedisTemplate().keys("het-test-" + tn + "-*");
						System.out.println(tn + "--" + keys.size());
						try {
							Thread.sleep(random.nextInt(3000));
						} catch (InterruptedException e) {
							System.err.println(e.getMessage());
						}
					}
				}
			}, "" + j).start();
		}
		System.in.read();
	}

	@Test
	@Ignore
	public void testObject() {
		cacheService.putCache("het-test", "okay", 200);
		System.out.println(cacheService.getCache("het-test"));
		Assert.assertTrue("okay".equals(cacheService.getCache("het-test")));
	}

	@Test
	@Ignore
	public void testHash() {
		cacheService.hput("het-test-hash", "het-key", "het-value");
		Assert.assertTrue("het-value".equals(cacheService.hget("het-test-hash", "het-key")));
	}

	@Test
	@Ignore
	public void testSomeHash() {
		String ckey = "het-test-hash-some";
		for (int i = 0; i < 10; i++) {
			cacheService.hput(ckey, "het-key" + i, "het-value" + i);
		}
		Assert.assertTrue("het-value".equals(cacheService.hget("het-test-hash", "het-key")));
	}

	@Test
	@Ignore
	public void testSet() {
		cacheService.sadd("het-test-set", "okay", "okay1");
	}

	@Test
	@Ignore
	public void testQueue() {
		cacheService.putQueue("het-test-queue", "okay");
	}

}
