update settings set hometext =
    E'<h1>Welcome to OzTrack Beta!</h1>\n' ||
    E'<p style="font-size:1.2em">\n' ||
    E'    OzTrack is a free-to-use web-based platform for the analysis and \n' ||
    E'    visualisation of animal tracking data. It was developed for the Australian \n' ||
    E'    animal tracking community but can be used to determine, measure and plot \n' ||
    E'    home-ranges for animals anywhere in the world.\n' ||
    E'</p>'
where hometext is null or hometext = '';

update settings set abouttext =
    E'<h1>About OzTrack</h1>\n' ||
    E'<p>\n' ||
    E'    The present OzTrack project is seeking support from the\n' ||
    E'    <a href="https://www.nectar.org.au/eresearch-tools">NeCTAR e-Research Tools</a>\n' ||
    E'    program for 2012-2013. The NeCTAR OzTrack project will build upon the work\n' ||
    E'    which was completed in the initial\n' ||
    E'    <a href="http://itee.uq.edu.au/~eresearch/projects/ands/oztrack/">OzTrack</a>\n' ||
    E'    project which was supported by the Australian National Data Service (ANDS)\n' ||
    E'    during 2010-2011.\n' ||
    E'</p>\n' ||
    E'<p><b>People involved in the NeCTAR OzTrack project are:</b></p>\n' ||
    E'<p>Project Steering Committee</p>\n' ||
    E'<ul>\n' ||
    E'    <li>Scientific Leader and Chair - Professor Craig Franklin, UQ;</li>\n' ||
    E'    <li>Technical Project Leader &ndash; Professor Jane Hunter, UQ;</li>\n' ||
    E'    <li>Project Manager &ndash; Wilfred Brimblecombe, UQ</li>\n' ||
    E'    <li>Dr Hamish Campbell, UQ</li>\n' ||
    E'    <li>Dr Colin Simpfendorfer, JCU</li>\n' ||
    E'    <li>Dr Toby Patterson, CSIRO</li>\n' ||
    E'    <li>Dr Greg Baxter, UQ</li>\n' ||
    E'    <li>Prof Mark Hindell, UTAS</li>\n' ||
    E'    <li>Dr David Westcott, CSIRO</li>\n' ||
    E'    <li>Prof Stuart Phinn</li>\n' ||
    E'    <li>NeCTAR representative</li>\n' ||
    E'</ul>\n' ||
    E'<p>Project Team</p>\n' ||
    E'<ul>\n' ||
    E'    <li>Project Manager &ndash; Wilfred Brimblecombe, UQ</li>\n' ||
    E'    <li>Scientific Data Analyst &ndash; Dr Ross Dwyer, UQ</li>\n' ||
    E'    <li>Software Engineer &ndash; Charles Brooking, UQ</li>\n' ||
    E'    <li>Independent testers from user community</li>\n' ||
    E'</ul>'
where abouttext is null or abouttext = '';

update settings set contacttext =
    E'<h1>Contacts</h1>\n' ||
    E'<div style="width:80%">\n' ||
    E'    <div style="position:relative;float:left;width:59%;">\n' ||
    E'        <h2>eResearch Lab, UQ</h2>\n' ||
    E'        <p>\n' ||
    E'            Jane Hunter<br>\n' ||
    E'            <a href="mailto:j.hunter@uq.edu.au">j.hunter@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'        <p>\n' ||
    E'            Wilfred Brimblecombe<br>\n' ||
    E'            <a href="mailto:w.brimblecombe@uq.edu.au">w.brimblecombe@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'        <p>\n' ||
    E'            Charles Brooking<br>\n' ||
    E'            <a href="mailto:c.brooking@uq.edu.au">c.brooking@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'        <p>\n' ||
    E'            Peggy Newman (former member)<br>\n' ||
    E'            <a href="mailto:peggy.newman@uq.edu.au">peggy.newman@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'        <h2>Environmental Decisions Hub, UQ</h2>\n' ||
    E'        <p>\n' ||
    E'            Matthew Watts<br>\n' ||
    E'            <a href="mailto:m.watts@uq.edu.au">m.watts@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'    </div>\n' ||
    E'    <div style="position:relative;float:right;width:39%;">\n' ||
    E'        <h2>Eco-Lab, UQ</h2>\n' ||
    E'        <p>\n' ||
    E'            Craig Franklin<br>\n' ||
    E'            <a href="mailto:c.franklin@uq.edu.au">c.franklin@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'        <p>\n' ||
    E'            Hamish Campbell<br>\n' ||
    E'            <a href="mailto:Hamish.Campbell@uq.edu.au">Hamish.Campbell@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'        <p>\n' ||
    E'            Ross Dwyer<br>\n' ||
    E'            <a href="mailto:ross.dwyer@uq.edu.au">ross.dwyer@uq.edu.au</a>\n' ||
    E'        </p>\n' ||
    E'    </div>\n' ||
    E'</div>'
where contacttext is null or contacttext = '';