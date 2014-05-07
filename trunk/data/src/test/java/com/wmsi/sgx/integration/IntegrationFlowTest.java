package com.wmsi.sgx.integration;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class IntegrationFlowTest extends AbstractTestNGSpringContextTests{

/*	private static final Logger log = LoggerFactory.getLogger(IntegrationFlowTest.class);
	
	@Autowired
	MessageChannel indexTickerChannel;
	
	@Autowired
	SubscribableChannel aggregationCompleteChannel;
	
	@Autowired
	PublishSubscribeChannel indexErrorChannel;
	
	@Autowired 
	IndexBuilderService indexBuilderServiceImpl;
	
	@Autowired
	private CapIQService capIQService;
		
	@Test
	public void testIndexTickerChannel() throws IndexerServiceException, InterruptedException, CapIQRequestException, ResponseParserException{
		
		CompanyInputRecord input = new CompanyInputRecord();
		input.setDate("2014-04-23");
		input.setId("Sunvic");
		input.setTicker("A75");
		
		CompanyInfo c = new CompanyInfo();
		c.setTickerCode("A75");
		
		when(capIQService.getCompanyInfo(any(String.class), any(String.class)))
		//.thenReturn(c);
		.thenThrow(new RuntimeException("Boom"));
		
		indexTickerChannel.send(
				MessageBuilder.withPayload(input)
				.setHeader("indexName", "test_index")
				.setHeader("jobDate", new Date())
				.setHeader("jobId", "123TestJob")
				.setHeader("sequenceSize", 1)
				//.setHeader("errorChannel", "indexErrorChannel")
				.build());
		
		indexErrorChannel.subscribe(new MessageHandler(){

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				log.error("Got Error message {}", message.toString());
			}
			
		});
	
		CountDownLatch latch = new CountDownLatch(1);
		
		TestMessageHandler handler = new TestMessageHandler(latch);
		aggregationCompleteChannel.subscribe(handler);

		// Wait a bit for message to be received
		boolean latchCountedToZero = latch.await(2000, TimeUnit.MILLISECONDS);

		if (!latchCountedToZero) {
			log.error(String.format("The specified waiting time of the latch (%s ms) elapsed.", 2000));			
		}
		
		assertTrue(latchCountedToZero);
		
	}

	private void verify(List<CompanyInputRecord> record){
		assertNotNull(record);
		assertEquals(record.size(), 1);				
		assertEquals(record.get(0).getTicker(), "A7S");		
	}

	class TestMessageHandler implements MessageHandler{
		private CountDownLatch latch;
		
		public TestMessageHandler(CountDownLatch l){
			latch = l;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message<?> message) throws MessagingException {
			log.error("Here");			
			verify((List<CompanyInputRecord>) message.getPayload());
			latch.countDown();
		}		
	}
	
	@Configuration
	@ImportResource("classpath:META-INF/spring/integration/spring-integration-context.xml")
	@Import(value={AppConfig.class})
	static class IntegrationFlowTestConfig {

		@Bean(name="indexBuilderServiceImpl")
		public IndexBuilderService indexBuilderServiceImpl(){
			return new IndexBuilderServiceImpl();
		}
		
		@Bean
		public CapIQService capIQService(){
			return mock(CapIQService.class);
		}
		
		@Bean
		public AlphaFactorIndexerService alphaFactorService(){
			return mock(AlphaFactorIndexerService.class);
		}
		
		@Bean
		public IndexerService indexerService(){
			IndexerService idx = mock(IndexerService.class);

			try{
				when(idx.save(any(String.class),  any(String.class),  any(Object.class),  any(String.class)))					
				.thenReturn(true);
			}
			catch(IndexerServiceException e){
				e.printStackTrace();
			}			

			return idx;
		}		
		
		@Bean(name="esRestTemplate")
		public RestTemplate esRestTemplate(){
			return mock(RestTemplate.class);
		}
		
	}
*/}
