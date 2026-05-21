select ssn,fname , sum(hours) from works_on group by essn having sum(hours) >(
    select AVG(hours) from (
        select sum(hours) from works_on group by essn
        from works_on
        group by essn
    )
)
order by essn;

