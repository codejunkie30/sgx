package com.wmsi.sgx.generator;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDGenerator implements IdentifierGenerator, Configurable {

	private static final Logger log = LoggerFactory.getLogger(IDGenerator.class);

	private String query;

	@Override
	public void configure(Type type, Properties params, Dialect d) throws MappingException {
		 String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);
		 query = "SELECT NEXT VALUE FOR " + tableName + "_seq";
	}
	
    /*
     * (non-Javadoc)
     * @see org.hibernate.id.IdentifierGenerator
            #generate(org.hibernate.engine.SessionImplementor, java.lang.Object)
     */
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
    	
    	try {
    	
    		final PreparedStatement pstmt = session.connection().prepareStatement(query);
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) return Long.parseLong(rs.getString(1));
    		
    	}
    	catch(Exception e) {
    		log.error("Trying to generate nextval: " + query, e);
    	}

        return null;
    }
    
    
}