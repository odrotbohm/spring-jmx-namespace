package org.springframework.jmx.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.jmx.config.LoggerSelector.CandidateSource;


/**
 * @author Oliver Gierke
 */
public class CandidateSourceUnitTest {

    CandidateSource source = new CandidateSource("org.foo",
            "org.foo.bar.FooBar", true);


    @Test
    public void testname() throws Exception {

        List<Candidate> result = source.getCandidatesUpToLevel(0);

        assertThat(result, hasItem(new Candidate("org.foo", 0)));
        assertThat(result, not(hasItem(new Candidate("org.foo.bar", 1))));
        assertThat(result, not(hasItem(new Candidate("org.foo.bar.FooBar", 2))));
    }


    @Test
    public void foo() throws Exception {

        List<Candidate> result = source.getCandidatesUpToLevel(1);

        assertThat(result, hasItem(new Candidate("org.foo", 0)));
        assertThat(result, hasItem(new Candidate("org.foo.bar", 1)));
        assertThat(result, not(hasItem(new Candidate("org.foo.bar.FooBar", 2))));
    }


    @Test
    public void bar() throws Exception {

        List<Candidate> result = source.getCandidatesUpToLevel(2);

        assertThat(result, hasItem(new Candidate("org.foo", 0)));
        assertThat(result, hasItem(new Candidate("org.foo.bar", 1)));
        assertThat(result, not(hasItem(new Candidate("org.foo.bar.FooBar", 2))));
    }
}
