import org.sonatype.nexus.ldap.persist.LdapConfigurationManager
import org.sonatype.nexus.ldap.persist.entity.LdapConfiguration
import org.sonatype.nexus.ldap.persist.entity.Connection
import org.sonatype.nexus.ldap.persist.entity.Mapping

def ldapConfigMgr = container.lookup(LdapConfigurationManager.class.getName());

def ldapConfig = ldapConfigMgr.newConfiguration();

ldapConfig.setName('epam')

connection = new Connection()
connection.setHost(new Connection.Host(Connection.Protocol.ldap, 'epam.com', 389))
connection.setAuthScheme('simple')
connection.setSystemUsername('auto_epm-rdua_doaas@epam.com')
connection.setSystemPassword('{{ nexus_autouser_password }}')
connection.setSearchBase('dc=epam,dc=com')
connection.setConnectionTimeout(30)
connection.setConnectionRetryDelay(300)
connection.setMaxIncidentsCount(3)
ldapConfig.setConnection(connection)

mapping = new Mapping()

mapping.setUserBaseDn('')
mapping.setUserObjectClass('user')
mapping.setUserIdAttribute('mail')
mapping.setUserRealNameAttribute('cn')
mapping.setEmailAddressAttribute('mail')
mapping.setLdapGroupsAsRoles(true)
mapping.setUserSubtree(true)
mapping.setUserMemberOfAttribute('memberof')
ldapConfig.setMapping(mapping)

ldapConfigMgr.addLdapServerConfiguration(ldapConfig)

security.addRole('Kharkiv', 'Admin', 'admin', ['nx-all'], ['nx-admin'])
