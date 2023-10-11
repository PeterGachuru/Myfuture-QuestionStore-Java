/**
 * 
 */
package ke.co.myfuture.Myfuture.dbconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

/**
 * @author Ramesh Fadatare
 * 
 */
@Configuration
public class WebMvcConfig
{
	
	@Bean
    public OpenEntityManagerInViewFilter studentManagementOpenEntityManagerInViewFilter()
    {
    	OpenEntityManagerInViewFilter osivFilter = new OpenEntityManagerInViewFilter();
    	osivFilter.setEntityManagerFactoryBeanName("studentManagementEntityManagerFactory");
    	return osivFilter;
    }

	@Bean
	public OpenEntityManagerInViewFilter questionsOpenEntityManagerInViewFilter()
	{
		OpenEntityManagerInViewFilter osivFilter = new OpenEntityManagerInViewFilter();
		osivFilter.setEntityManagerFactoryBeanName("questionStoreEntityManagerFactory");
		return osivFilter;
	}

	@Bean
	public OpenEntityManagerInViewFilter imagesOpenEntityManagerInViewFilter()
	{
		OpenEntityManagerInViewFilter osivFilter = new OpenEntityManagerInViewFilter();
		osivFilter.setEntityManagerFactoryBeanName("imagesEntityManagerFactory");
		return osivFilter;
	}
}
