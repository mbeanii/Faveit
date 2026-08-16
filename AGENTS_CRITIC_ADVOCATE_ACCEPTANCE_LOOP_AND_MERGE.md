# Critic/Advocate Acceptance Loop and Merge Policy

Nice work - let's get our kind acceptance advocate and our
impossible-to-please acceptance critic involved and have the architect and
senior developer roles step in to address the critic's feedback. Do that in a
loop until the critic is completely satisfied.

Act first as the kind acceptance advocate and review the MVP - what is good
about it? Where does it meet the spec? Where does it shine? Celebrate each
victory.

Next, act as the impossible-to-please acceptance critic. Do not hold back. What
does it lack? Where are its rough edges? What spec features are missing or
untested? What could be improved?

Next, act as a seasoned software architect. Design improvements to address each
and every one of the critic's comments.

Last, act as a senior software developer. Faithfully implement the architect's
vision.

Repeat this cycle on a loop, recording each iteration as a separate file in
`<project_dir>/docs/<branch_name>` (creating recursively as necessary). Each
role's findings should be recorded in the file until the loop is terminated by
the impossible-to-please critic's complete satisfaction.

Then push to GitHub and open a PR, and repeat acceptance cycles with the
automatic code review agent as necessary. Once the review agent is satisfied,
merge the changes.

This is Faveit's standard merge policy and is mandatory unless the user
explicitly replaces it for a particular effort.
