import org.sonatype.nexus.blobstore.api.BlobStoreManager
import org.sonatype.nexus.repository.storage.WritePolicy
import org.sonatype.nexus.repository.maven.VersionPolicy
import org.sonatype.nexus.repository.maven.LayoutPolicy


List<String> repos = ['iEat-maven', 'iEat-jcenter']*.toString();

security.securitySystem.changePassword('admin',"{{ nexus_admin_password }}");

repository.createMavenProxy('iEat-maven', 'https://repo1.maven.org/maven2/', 'default', true, org.sonatype.nexus.repository.maven.VersionPolicy.MIXED, org.sonatype.nexus.repository.maven.LayoutPolicy.STRICT )
repository.createMavenProxy('iEat-jcenter', 'https://jcenter.bintray.com', 'default', true, org.sonatype.nexus.repository.maven.VersionPolicy.MIXED, org.sonatype.nexus.repository.maven.LayoutPolicy.STRICT )
repository.createRawProxy('iEat-liqpay', 'https://github.com/liqpay/sdk-java/raw/repository', 'default', true)
repository.createMavenHosted('iEat-artifacts', BlobStoreManager.DEFAULT_BLOBSTORE_NAME, false, VersionPolicy.MIXED, WritePolicy.ALLOW_ONCE, LayoutPolicy.PERMISSIVE)
repository.createMavenGroup('iEat', repos, BlobStoreManager.DEFAULT_BLOBSTORE_NAME )
